package idc.inbound.controller;

import idc.inbound.dto.vision.UserDTO;
import idc.inbound.dto.UserMeDTO;
import idc.inbound.request.SignUpRequest;
import idc.inbound.request.UserFirstLoginRequestModel;
import idc.inbound.secure.SecurityConstants;
import idc.inbound.service.vision.UserService;
import idc.inbound.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final Utils utils;

    @Value("${COOKIE_DOMAIN:}")
    private String cookieDomain;

    @PostMapping(path = "/sign-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerNewUsers(@Valid @RequestBody SignUpRequest requestModel){
        return ResponseEntity.ok(userService.signUp(requestModel));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeDTO> getMe() {
        UserMeDTO dto = userService.getCurrentUserInfo(null);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(path = "/updateEmployee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEmployees(@Valid @RequestBody SignUpRequest requestModel){
        userService.updateEmployees(requestModel);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/pass-reset/{expertis}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resetPassword(@PathVariable("expertis") String expertis){
        return ResponseEntity.ok(userService.resetPassword(expertis));
    }

    @PostMapping(path = "/first-login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserMeDTO> firstLoginReset(@Valid @RequestBody UserFirstLoginRequestModel requestModel, HttpServletRequest req, HttpServletResponse res){
        String expertis = userService.firstLogin(requestModel);

        String accessToken = utils.generateAccessToken(requestModel.login(), req);
        addCookiesToResponse(res, accessToken);

        UserMeDTO dto = userService.getCurrentUserInfo(expertis);

        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/getAllUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(path = "/get-user/{expertis}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserByExpertis(@PathVariable("expertis") String expertis){
        return ResponseEntity.ok(userService.getUserByExpertis(expertis));
    }

    @GetMapping(path = "/getAllInactiveUsers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getAllInactiveUsers(){
        return ResponseEntity.ok(userService.getAllInactiveUsers());
    }


    private void addCookiesToResponse(HttpServletResponse res, String accessToken) {
        ResponseCookie access = ResponseCookie.from("AccessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(cookieDomain)
                .sameSite("Strict")
                .maxAge(SecurityConstants.ACCESS_TOKEN_EXPIRATION)
                .build();

        res.addHeader(SET_COOKIE, access.toString());
        log.debug("Set AccessToken и RefreshToken cookies с SameSite=Strict, Domain=$");
    }
}
