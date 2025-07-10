package idc.inbound.service.vision;

import idc.inbound.dto.vision.UserDTO;
import idc.inbound.dto.UserMeDTO;
import idc.inbound.request.SignUpRequest;
import idc.inbound.request.UserFirstLoginRequestModel;
import jakarta.annotation.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserMeDTO getCurrentUserInfo(@Nullable String expertis);
    UserDetails loadUserByUsernameWithoutPassword(String username);
    String signUp(SignUpRequest requestModel);
    void updateEmployees(SignUpRequest requestModel);
    String resetPassword(String expertis);
    String firstLogin(UserFirstLoginRequestModel requestModel);
    List<UserDTO> getAllUsers();
    List<UserDTO> getAllInactiveUsers();

    UserDTO getUserByExpertis(String expertis);
    UserDTO getUserByLogin(String login);
}
