package idc.inbound.secure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import idc.inbound.customError.AuthenticationFailedException;
import idc.inbound.customError.InvalidTokenException;
import idc.inbound.customError.TooManyRequestsException;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static idc.inbound.secure.SecurityConstants.*;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final ConcurrentHashMap<String, BucketWrapper> buckets = new ConcurrentHashMap<>();
    private final String COOKIE_DOMAIN;
    private final Utils utils;
    private final UserService userService;

    public AuthorizationFilter(AuthenticationManager authenticationManager, String COOKIE_DOMAIN, Utils utils, UserService userService) {
        super(authenticationManager);

        this.COOKIE_DOMAIN = COOKIE_DOMAIN;
        this.utils = utils;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String uri = request.getRequestURI();

            if (isPublicUri(uri)) {
                chain.doFilter(request, response);
                return;
            }

            String requestIp = utils.getClientIp(request);
            Bucket bucket = resolveBucket(requestIp);

            if (!bucket.tryConsume(1)) {
                throw new TooManyRequestsException("Too many requests. Please try again later.");
            }

            String accessToken = getCookieValue(request, "AccessToken");

            if (accessToken == null) {
                throw new InvalidTokenException("Access token is missing.");
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } else {
                throw new InvalidTokenException("Invalid access token.");
            }

        } catch (InvalidTokenException | AuthenticationFailedException ex) {
            log.info("Authentication error: {}", ex.getMessage());

            clearCookies(response);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", ex.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = getCookieValue(request, "AccessToken");

        if (accessToken == null) {
            log.warn("Access token not found in cookies.");
            return null;
        }

        try {
            Claims claims = utils.parseToken(accessToken);

            if (claims == null) {
                throw new InvalidTokenException("Invalid JWT token - claims are null.");
            }

            String userId = claims.getSubject();

//            String tokenIp = claims.get("ip", String.class);
//            String requestIp = utils.getClientIp(request);
//
//            if (!requestIp.equals(tokenIp)) {
//                log.warn("IP mismatch detected for userId: {}. Invalidating all tokens.", userId);
//                throw new InvalidTokenException("Invalid JWT token - mismatch in IP");
//            }

            if (userId == null) {
                throw new InvalidTokenException("Invalid JWT token - subject is null.");
            }

            CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsernameWithoutPassword(userId);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (ExpiredJwtException e) {
            throw new AuthenticationFailedException("Expired JWT token.");
        } catch (JwtException e) {
            throw new AuthenticationFailedException("Invalid JWT token.");
        }
    }

    private boolean isPublicUri(String uri) {
        return uri.equals("/swagger-ui/index.html")
                || uri.contains("/api/v1/users/first-login")
                || uri.startsWith("/actuator")
                || uri.contains("/favicon.ico");
    }

    private Bucket resolveBucket(String key) {
        BucketWrapper wrapper = buckets.computeIfAbsent(key, k -> new BucketWrapper(
                Bucket.builder()
                        .addLimit(limit -> limit.capacity(BUCKET_CAPACITY).refillGreedy(BUCKET_REFILL_TOKENS, Duration.ofMinutes(1)))
                        .build()
        ));

        wrapper.updateLastAccessed();
        return wrapper.getBucket();
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            log.debug("No cookies found in the request.");
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        log.debug("Cookie {} not found in the request.", cookieName);
        return null;
    }

    @Scheduled(fixedRate = 600000)
    private void cleanupBuckets() {
        long expirationTime = System.currentTimeMillis() - Duration.ofMinutes(INACTIVITY_CLEANUP_MINUTES).toMillis();
        buckets.entrySet().removeIf(entry -> entry.getValue().getLastAccessed() < expirationTime);
        log.info("Cleanup completed. Active buckets: {}", buckets.size());
    }

    private void clearCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie.from("AccessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader(SET_COOKIE, clearAccess.toString());

        log.debug("Cleared AccessToken and RefreshToken cookies");
    }
}
