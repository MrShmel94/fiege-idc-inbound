package idc.inbound.secure.aspect;

import idc.inbound.secure.CustomUserDetails;
import idc.inbound.service.vision.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Set;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class AccessControlAspect {

    private final UserService userService;

    /**
     * Enforces access control before method execution based on the @AccessControl annotation.
     * Validates user roles, weight, department, and site access.
     *
     * @param joinPoint The join point representing the intercepted method.
     * @param accessControl The annotation containing access control rules.
     */
    @Before("@annotation(accessControl)")
    public void enforceAccess(JoinPoint joinPoint, AccessControl accessControl) {

        String username = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            username = userDetails.getUsername();
        }

        if (username == null) {
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof Principal principal) {
                    username = principal.getName();
                    break;
                }
            }
        }

        if (username == null) {
            throw new AccessDeniedException("Unable to determine user for access control.");
        }

        if (accessControl.fullAccess()) {
            log.info("Full access granted for user: {}", username);
            return;
        }

        CustomUserDetails user = (CustomUserDetails) userService.loadUserByUsernameWithoutPassword(username);

        int maxWeight = user.role().getWeight();

        if (maxWeight < accessControl.minWeight()) {
            throw new AccessDeniedException("Insufficient role weight. Required: " + accessControl.minWeight());
        }

        if (accessControl.allowedRoles().length > 0) {
            String userRoles = user.role().getName();

            Set<String> requiredRoles = Set.of(accessControl.allowedRoles());

            if (requiredRoles.contains(userRoles)) {
                throw new AccessDeniedException("Access denied: required roles not met.");
            }
        }

        log.info("Access granted for user: {}", username);
    }
}
