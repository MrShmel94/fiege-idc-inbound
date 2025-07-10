package idc.inbound.secure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import idc.inbound.customError.*;
import idc.inbound.dto.UserMeDTO;
import idc.inbound.request.UserLoginRequestModel;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final String COOKIE_DOMAIN;
    private final Utils utils;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, String COOKIE_DOMAIN, Utils utils, UserService userService) {
        super(authenticationManager);
        this.COOKIE_DOMAIN = COOKIE_DOMAIN;
        this.utils = utils;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            log.debug("Attempting authentication for request: {}", req.getRequestURI());

            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
            log.debug("Authentication attempt with login: {}", creds.login());

            if (creds.login() == null || creds.login().trim().isEmpty()) {
                sendErrorResponse(res, HttpStatus.BAD_REQUEST,
                        "Login is required",
                        "Please check your login.");
                return null;
            }
            if (creds.password() == null || creds.password().trim().isEmpty()) {
                sendErrorResponse(res, HttpStatus.BAD_REQUEST,
                        "Password is required",
                        "Please check your password.");
                return null;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    creds.login(), creds.password(), new ArrayList<>());

            return getAuthenticationManager().authenticate(authenticationToken);

        } catch (JsonParseException e) {
            log.error("Malformed JSON in request body", e);
            throw new InvalidLoginRequestException("Invalid JSON format in login request", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error("Failed to read user login request model", e);
            throw new InvalidLoginRequestException("Invalid login request data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Authentication process failed: {}", e.getMessage());
            sendErrorResponse(res, HttpStatus.UNAUTHORIZED,
                    "Authentication processing error",
                    "Please check your credentials (email and password).");
        }
        return null;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.error("Authentication failed for request URI: {}, reason: {}", request.getRequestURI(), failed.getMessage());

        try {
            if ("BadCredentialsException".equals(failed.getClass().getSimpleName())) {
                handleException(response, new InvalidLoginRequestException("Invalid credentials provided. Please try again.", HttpStatus.UNAUTHORIZED));
            } else {
                handleException(response, new CustomAuthenticationException("Authentication failed. Please check your credentials.", HttpStatus.UNAUTHORIZED));
            }
        } catch (IOException e) {
            log.error("Error writing the authentication failure response: {}", e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException {

        log.info("Successful authentication initiated for request: {}", req.getRequestURI());

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

            if (customUserDetails.isFirstLogin()) {
                res.setStatus(HttpStatus.OK.value());
                res.setContentType("application/json");
                res.getWriter().write("{\"firstLogin\":true}");
                log.info("First login for user: {} — password change required.", customUserDetails.getUsername());
                return;
            }

            String accessToken = utils.generateAccessToken(customUserDetails.getUsername(), req);

            addCookiesToResponse(res, accessToken);

            SecurityContextHolder.getContext().setAuthentication(auth);

            UserMeDTO dto = userService.getCurrentUserInfo(null);

            res.setStatus(HttpStatus.OK.value());
            res.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.writeValue(res.getWriter(), dto);

            log.info("Successful authentication for user: {}", customUserDetails.getUsername());
        } catch (TokenGenerationException e) {
            log.error("Failed to generate tokens for successful authentication.", e);
            handleException(res, new AuthenticationProcessingException("Error generating tokens after authentication."));
        }
    }

    private void sendErrorResponse(HttpServletResponse res, HttpStatus status, String error, String message) {
        try {
            res.setStatus(status.value());
            res.setContentType("application/json");

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", status.value());
            errorDetails.put("error", error);
            errorDetails.put("message", message);
            errorDetails.put("timestamp", LocalDateTime.now());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            res.getWriter().write(mapper.writeValueAsString(errorDetails));

            log.info("Error response sent: {} - {}", status.value(), message);
        } catch (IOException ex) {
            log.error("Failed to send error response: {}", ex.getMessage());
        }
    }

    private void addCookiesToResponse(HttpServletResponse res, String accessToken) {
        ResponseCookie access = ResponseCookie.from("AccessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .sameSite("Strict")
                .maxAge(SecurityConstants.ACCESS_TOKEN_EXPIRATION)
                .build();

        res.addHeader(SET_COOKIE, access.toString());
        log.debug("Set AccessToken и RefreshToken cookies с SameSite=Strict, Domain=$");
    }

    private void handleException(HttpServletResponse response, CustomException exception) throws IOException {
        response.setStatus(exception.getStatus().value());
        response.setContentType("application/json");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", exception.getStatus().value());
        errorDetails.put("error", exception.getStatus().getReasonPhrase());
        errorDetails.put("message", exception.getErrorMessage());
        errorDetails.put("timestamp", LocalDateTime.now());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorDetails));

        log.error("Authentication error: {}", exception.getErrorMessage());
    }
}
