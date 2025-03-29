package com.fpt.shopapp.controller;

import com.fpt.shopapp.dto.RefreshTokenDTO;
import com.fpt.shopapp.dto.UpdateUserDTO;
import com.fpt.shopapp.dto.UserDTO;
import com.fpt.shopapp.dto.UserLoginDTO;
import com.fpt.shopapp.model.Token;
import com.fpt.shopapp.model.User;
import com.fpt.shopapp.responses.LoginResponse;
import com.fpt.shopapp.responses.RegisterResponse;
import com.fpt.shopapp.responses.UserResponse;
import com.fpt.shopapp.services.TokenService;
import com.fpt.shopapp.services.UserService;
import com.fpt.shopapp.components.LocalizationUtils;
import com.fpt.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LocalizationUtils localizationUtils;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
            ){
        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse);
        }

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            registerResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
            return ResponseEntity.badRequest().body(registerResponse);
        }

        try {
            User user = userService.createUser(userDTO);
            registerResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY));
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse);
        } catch (Exception e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request){
        try {
            // Kiểm tra thông tin đăng nhập và sinh token
            // Trả về token trong response
            String token = userService.login(userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(), userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());
            String userAgent = request.getHeader("User-Agent");
            User userDetail = userService.getUserDetailFromToken(token);
            Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            User userDetail = userService.getUserDetailFromToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Refresh token successfully")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage())).build());
        }
    }

    private boolean isMobileDevice(String userAgent) {
        return userAgent.toLowerCase().contains("mobile");
    }

    @PostMapping("/details")
    public ResponseEntity<UserResponse>  getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.substring(7); //Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailFromToken(extractedToken);
            if (!user.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e ) {
            return ResponseEntity.badRequest().build();
        }
    }
}
