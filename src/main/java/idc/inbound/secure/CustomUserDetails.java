package idc.inbound.secure;

import idc.inbound.dto.vision.RoleDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.service.annotation.GetExchange;


import java.util.Collection;
import java.util.List;


public record CustomUserDetails(
        @Getter
        Integer id,
        String expertis,
        String login,
        String password,
        RoleDTO role,
        boolean isActive,
        boolean isFirstLogin
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
