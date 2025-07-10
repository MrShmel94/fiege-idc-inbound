package idc.inbound.serviceImpl.vision;

import idc.inbound.customError.AlreadyExistsException;
import idc.inbound.customError.AuthenticationFailedException;
import idc.inbound.customError.NotFoundException;
import idc.inbound.dto.vision.RoleDTO;
import idc.inbound.dto.vision.UserDTO;
import idc.inbound.dto.UserMeDTO;
import idc.inbound.entity.vision.*;
import idc.inbound.redis.RedisCacheService;
import idc.inbound.repository.vision.UserRepository;
import idc.inbound.request.SignUpRequest;
import idc.inbound.request.UserFirstLoginRequestModel;
import idc.inbound.secure.CustomUserDetails;
import idc.inbound.secure.SecurityUtils;
import idc.inbound.service.vision.RoleService;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisCacheService redisCacheService;
    private final RoleService roleService;
    private final SecurityUtils securityUtils;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserMeDTO getCurrentUserInfo(@Nullable String expertis) {

        CustomUserDetails userDetails = null;

        if(expertis == null){
            userDetails = securityUtils.getCurrentUser();
        }

        UserDTO user = getUserByExpertis(expertis == null ? userDetails.expertis() : expertis);

        return new UserMeDTO(
                user.getName(), user.getSecondName(), user.getDepartmentName(), user.getPositionName(), user.getExpertis(), user.getRoleName()
        );
    }

    @Override
    public UserDetails loadUserByUsernameWithoutPassword(String username) {
        String expertis = redisCacheService.getFromHash("mapping_login_idc", username, String.class).orElse(null);
        UserDTO userDTO = null;

        if(expertis == null){
            userDTO = getUserByLogin(username);
            redisCacheService.saveToHash("mapping_login_idc", username, userDTO.getExpertis());
        }else{
            userDTO = getUserByExpertis(expertis);
        }

        List<RoleDTO> roles = roleService.getAllRoles();

        UserDTO finalUserDTO = userDTO;
        RoleDTO currentRole = roles.stream().filter(dto -> dto.getName().equals(finalUserDTO.getRoleName())).findFirst().orElseThrow(() -> new NotFoundException("Role not found"));
        return new CustomUserDetails(
                userDTO.getId(), userDTO.getExpertis(), userDTO.getLogin(), "", currentRole, userDTO.getIsActive(), userDTO.getIsFirstLogin()
        );
    }

    @Override
    @Transactional
    public String signUp(SignUpRequest requestModel) {
        Optional<UserDTO> optionalUser = userRepository.findByExpertisAndLogin(requestModel.getExpertis(), requestModel.getLogin());

        if (optionalUser.isPresent()) {
            throw new AlreadyExistsException("User with expertis or login already exists");
        }

        User user = updateOrSaveUser(requestModel);

        CustomUserDetails userDetails = securityUtils.getCurrentUser();
        user.setCreatedByUser(entityManager.getReference(User.class, userDetails.getId()));

        String rawPassword = Utils.generatePassword(8);
        String hashedPassword = bCryptPasswordEncoder.encode(rawPassword);

        user.setEncryptedPassword(hashedPassword);
        user.setIsFirstLogin(true);
        user.setActive(true);

        userRepository.save(user);

        return rawPassword;
    }

    private User updateOrSaveUser(SignUpRequest requestModel) {
        CustomUserDetails userDetails = securityUtils.getCurrentUser();

        setCurrentUserId(userDetails.getId());

        User user = new User();
        user.setName(requestModel.getName());
        user.setSecondName(requestModel.getSecondName());
        user.setExpertis(requestModel.getExpertis());
        user.setLogin(requestModel.getLogin());
        user.setPosition(entityManager.getReference(Position.class, requestModel.getPositionId()));
        user.setDepartment(entityManager.getReference(Department.class, requestModel.getDepartmentId()));
        user.setRole(entityManager.getReference(Role.class, requestModel.getRoleId()));

        return user;
    }

    @Override
    @Transactional
    public void updateEmployees(SignUpRequest requestModel) {
        Optional<UserDTO> optionalUser = userRepository.findByExpertisAndLogin(requestModel.getExpertis(), requestModel.getLogin());

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User with expertis or login not exists");
        }

        User user = updateOrSaveUser(requestModel);
        userRepository.save(user);
        redisCacheService.deleteFromHash("userDetailsIDC:hash", user.getExpertis());
    }

    @Override
    @Transactional
    public String resetPassword(String expertis) {
        Optional<User> user = userRepository.findByExpertis(expertis);

        if(user.isEmpty()) {
            throw new NotFoundException("User with expertis not exists");
        }

        CustomUserDetails userDetails = securityUtils.getCurrentUser();
        setCurrentUserId(userDetails.getId());

        String rawPassword = Utils.generatePassword(8);
        String hashedPassword = bCryptPasswordEncoder.encode(rawPassword);

        User userEntity = user.get();

        userEntity.setEncryptedPassword(hashedPassword);
        userEntity.setIsFirstLogin(true);

        userRepository.save(userEntity);
        redisCacheService.deleteFromHash("userDetailsIDC:hash", expertis);

        return rawPassword;
    }

    @Override
    @Transactional
    public String firstLogin(UserFirstLoginRequestModel requestModel) {
        Optional<User> userOptional = userRepository.findByLogin(requestModel.login());

        if(userOptional.isEmpty()) {
            throw new NotFoundException("User with login not exists");
        }

        User user = userOptional.get();

        setCurrentUserId(user.getId());

        String rawPassword = requestModel.firstPassword();
        String hashedPasswordFromDb = user.getEncryptedPassword();

        if (!bCryptPasswordEncoder.matches(rawPassword, hashedPasswordFromDb)) {
            throw new AuthenticationFailedException("Invalid old password");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(requestModel.password());
        user.setEncryptedPassword(hashedPassword);
        user.setIsFirstLogin(false);
        userRepository.save(user);

        redisCacheService.deleteFromHash("userDetailsIDC:hash", user.getExpertis());

        return user.getExpertis();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllActiveUsers();
    }

    @Override
    public List<UserDTO> getAllInactiveUsers() {
        return userRepository.findAllInactiveUsers();
    }

    @Override
    public UserDTO getUserByExpertis(String expertis) {

        if(expertis == null){
            throw new UsernameNotFoundException("Expertis is null");
        }

        UserDTO userDTO = redisCacheService.getFromHash("userDetailsIDC:hash", expertis, UserDTO.class).orElse(null);

        if(userDTO == null){
            userDTO = userRepository.findDTOByExpertis(expertis).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            redisCacheService.saveToHash("userDetailsIDC:hash", expertis, userDTO);
        }

        return userDTO;
    }

    @Override
    public UserDTO getUserByLogin(String login) {

        if(login == null){
            throw new UsernameNotFoundException("Login is null");
        }

        String expertis = redisCacheService.getFromHash("mapping_login_idc", login, String.class).orElse(null);
        UserDTO userDTO = null;

        if(expertis == null){
            userDTO = userRepository.findDTOByUsername(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            redisCacheService.saveToHash("mapping_login_idc", login, userDTO.getExpertis());
        }else{
            userDTO = getUserByExpertis(expertis);
        }

        return userDTO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDTO userDTO = getUserByLogin(username);
        String password = tryGetPasswordOnlyIfNeeded(userDTO.getId());

        List<RoleDTO> roles = roleService.getAllRoles();
        RoleDTO currentRole = roles.stream().filter(dto -> dto.getName().equals(userDTO.getRoleName())).findFirst().orElseThrow(() -> new NotFoundException("Role not found"));
        return new CustomUserDetails(
                userDTO.getId(), userDTO.getExpertis(), userDTO.getLogin(), password, currentRole, userDTO.getIsActive(), userDTO.getIsFirstLogin()
        );
    }

    private String tryGetPasswordOnlyIfNeeded(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return getUserEncryptedPassword(userId).orElseThrow(() -> new UsernameNotFoundException(
                    String.format("User %s not found", userId)));
        }
        return "";
    }

    private Optional<String> getUserEncryptedPassword(int userId) {
        return userRepository.getUserEncryptedPassword(userId);
    }

    private void setCurrentUserId(Integer userId) {
        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, false)")
                .setParameter("userId", String.valueOf(userId))
                .getSingleResult();
    }
}
