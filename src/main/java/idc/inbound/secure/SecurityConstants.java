package idc.inbound.secure;

public class SecurityConstants {
    public static final long ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L;
    public static final int BUCKET_CAPACITY = 200;
    public static final int BUCKET_REFILL_TOKENS = 100;
    public static final int INACTIVITY_CLEANUP_MINUTES = 30;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SIGN_UP_URL = "/api/v1/users/sign-up";
    public static final String FIRST_LOGIN_URL = "/api/v1/users/first-login";
}