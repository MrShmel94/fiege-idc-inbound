package idc.inbound.secure.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessControl {
    /**
     * Minimum required role weight for access.
     */
    int minWeight() default 0;

    /**
     * Specifies roles that are allowed to access the method.
     */
    String[] allowedRoles() default {};

    /**
     * If set to true, this method bypasses all access restrictions (e.g., for Admins).
     */
    boolean fullAccess() default false;
}
