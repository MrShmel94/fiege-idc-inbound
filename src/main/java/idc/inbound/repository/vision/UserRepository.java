package idc.inbound.repository.vision;

import idc.inbound.dto.vision.UserDTO;
import idc.inbound.entity.vision.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
           SELECT new idc.inbound.dto.vision.UserDTO(
           us.id, us.name, us.secondName, us.expertis, us.login, us.isFirstLogin, us.isActive, us.position.name, us.department.name, CONCAT(us.createdByUser.name, ' ', us.createdByUser.secondName), us.role.name
           ) FROM User us
           WHERE us.login = :login
           """)
    Optional<UserDTO> findDTOByUsername(@Param("login") String username);

    Optional<User> findByLogin(String login);

    Optional<User> findByExpertis(String expertis);

    @Query("""
           SELECT new idc.inbound.dto.vision.UserDTO(
           us.id, us.name, us.secondName, us.expertis, us.login, us.isFirstLogin, us.isActive, us.position.name, us.department.name, CONCAT(us.createdByUser.name, ' ', us.createdByUser.secondName), us.role.name
           ) FROM User us
           WHERE us.expertis = :expertis
           """)
    Optional<UserDTO> findDTOByExpertis(@Param("expertis") String expertis);

    @Query("""
           SELECT new idc.inbound.dto.vision.UserDTO(
           us.id, us.name, us.secondName, us.expertis, us.login, us.isFirstLogin, us.isActive, us.position.name, us.department.name, CONCAT(us.createdByUser.name, ' ', us.createdByUser.secondName), us.role.name
           ) FROM User us
           WHERE us.expertis = :expertis
           OR us.login = :login
           """)
    Optional<UserDTO> findByExpertisAndLogin(@Param("expertis") String expertis, @Param("login") String login);

    @Query(value = """
           SELECT ue.encrypted_password
           FROM vision_idc.users AS ue
           WHERE ue.id = :userId
           """, nativeQuery = true)
    Optional<String> getUserEncryptedPassword (@Param("userId") int userId);

    @Query("""
           SELECT new idc.inbound.dto.vision.UserDTO(
           us.id, us.name, us.secondName, us.expertis, us.login, us.isFirstLogin, us.isActive, us.position.name, us.department.name, CONCAT(us.createdByUser.name, ' ', us.createdByUser.secondName), us.role.name
           ) FROM User us
           WHERE us.isActive = true
           """)
    List<UserDTO> findAllActiveUsers();

    @Query("""
           SELECT new idc.inbound.dto.vision.UserDTO(
           us.id, us.name, us.secondName, us.expertis, us.login, us.isFirstLogin, us.isActive, us.position.name, us.department.name, CONCAT(us.createdByUser.name, ' ', us.createdByUser.secondName), us.role.name
           ) FROM User us
           WHERE us.isActive = false
           """)
    List<UserDTO> findAllInactiveUsers();
}
