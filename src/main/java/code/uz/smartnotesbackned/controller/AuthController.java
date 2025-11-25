package code.uz.smartnotesbackned.controller;

import code.uz.smartnotesbackned.dto.*;
import code.uz.smartnotesbackned.dto.auth.*;
import code.uz.smartnotesbackned.dto.profile.ProfileResponseDTO;
import code.uz.smartnotesbackned.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/api/v1")
@RequiredArgsConstructor
@Tag(
        name = "Authentication Controller",
        description = "Handles user registration, email verification, login, and password reset operations."
)
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user by validating input details such as email, password, name and password "
                    + "After successful registration, a verification code is sent to the user's email."
    )
    @PostMapping("/register")
    public ResponseEntity<AppResponse<String>> register(@RequestBody @Valid RegisterDTO dto) {
        log.info("Registration attempt for email: {}", dto.getEmail());
        return ResponseEntity.ok(authService.register(dto));
    }

    @Operation(
            summary = "Verify registration email",
            description = "Verifies the user's registration by validating the email verification code sent to their email address."
    )
    @PostMapping("/register/email/verification")
    public ResponseEntity<AppResponse<String>> verifyRegistrationCode(@RequestBody @Valid EmailVerificationDTO dto) {
        log.info("Email verification attempt for email: {}", dto.getEmail());
        return ResponseEntity.ok(authService.verifyRegistration(dto));
    }

    @Operation(
            summary = "Resend email verification code",
            description = "Resends the email verification code to the user's registered email in case they didn’t receive or the code expired."
    )
    @PostMapping("/register/email/verification/resend")
    public ResponseEntity<AppResponse<String>> emailVerificationResend(@RequestBody @Valid EmailResendDTO dto) {
        log.info("Resend verification code request for email: {}", dto.getEmail());
        return ResponseEntity.ok(authService.resendCode(dto));
    }

    @Operation(
            summary = "User login",
            description = "Authenticates the user using their credentials (email and password) and returns the profile details along with an access token."
    )
    @PostMapping("/login")
    public ResponseEntity<ProfileResponseDTO> login(@RequestBody @Valid AuthDTO dto) {
        log.info("Login attempt for user: {}", dto.getEmail());
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(
            summary = "Request password reset",
            description = "Reset password by sending verification code to user's email"
    )
    @PostMapping("/reset/password")
    public ResponseEntity<AppResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        log.info("Password reset request for email: {}", dto.getEmail());
        return ResponseEntity.ok(authService.resetPassword(dto));
    }

    @Operation(
            summary = "Confirm password reset",
            description = "Verifies received code from email and changes password"
    )
    @PostMapping("reset/password/confirm")
    public ResponseEntity<AppResponse<String>> resetPasswordConfirm(@RequestBody @Valid ResetPasswordConfirmDTO dto) {
        log.info("Password reset confirmation for email: {}", dto.getEmail());
        return ResponseEntity.ok(authService.resetPasswordConfirm(dto));
    }
}
