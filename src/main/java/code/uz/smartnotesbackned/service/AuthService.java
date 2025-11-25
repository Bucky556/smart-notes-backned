package code.uz.smartnotesbackned.service;

import code.uz.smartnotesbackned.config.CustomUserDetails;
import code.uz.smartnotesbackned.dto.*;
import code.uz.smartnotesbackned.dto.auth.*;
import code.uz.smartnotesbackned.dto.profile.ProfileResponseDTO;
import code.uz.smartnotesbackned.entity.ProfileEntity;
import code.uz.smartnotesbackned.entity.ProfileRoleEntity;
import code.uz.smartnotesbackned.enums.EmailType;
import code.uz.smartnotesbackned.enums.GeneralStatus;
import code.uz.smartnotesbackned.enums.Role;
import code.uz.smartnotesbackned.exception.BadException;
import code.uz.smartnotesbackned.repository.ProfileRepository;
import code.uz.smartnotesbackned.repository.ProfileRoleRepository;
import code.uz.smartnotesbackned.util.EmailUtil;
import code.uz.smartnotesbackned.util.JwtUtil;
import code.uz.smartnotesbackned.util.RandomUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ProfileRepository profileRepository;
    private final ProfileRoleService profileRoleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendService emailSendService;
    private final EmailHistoryService emailHistoryService;
    private final AuthenticationManager authenticationManager;
    private final ProfileRoleRepository profileRoleRepository;
    private final AttachService attachService;

    public AppResponse<String> register(@Valid RegisterDTO dto) {
        String code = RandomUtil.generateRandomCode();

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BadException("Passwords do not match");
        }

        Optional<ProfileEntity> byEmail = profileRepository.findByEmailAndVisibleTrue(dto.getEmail());
        if (byEmail.isPresent()) {
            ProfileEntity profileEntity = byEmail.get();
            if (profileEntity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profileEntity.getId());
                profileRepository.delete(profileEntity);
            } else {
                throw new BadException("User already exists");
            }
        }
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setStatus(GeneralStatus.IN_REGISTRATION);
        profileRepository.save(entity);

        profileRoleService.create(entity.getId(), List.of(Role.ROLE_USER));

        if (EmailUtil.isEmail(dto.getEmail())) {
            emailSendService.sendHtmlEmail(dto.getEmail(), dto.getName(), code, EmailType.REGISTRATION);
            return new AppResponse<>("Email verification sent");
        }
        return new AppResponse<>("Invalid email");
    }

    public AppResponse<String> verifyRegistration(@Valid EmailVerificationDTO dto) {
        Optional<ProfileEntity> profileEntity = profileRepository.findByEmailAndVisibleTrue(dto.getEmail());
        if (profileEntity.isEmpty()) {
            throw new BadException("Profile does not exist");
        }

        ProfileEntity entity = profileEntity.get();
        if (!entity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new BadException("Verification failed");
        }

        emailHistoryService.checkCode(dto.getEmail(), dto.getCode());

        profileRepository.updateStatusById(GeneralStatus.ACTIVE, entity.getId());

        return new AppResponse<>("Verification confirmed");
    }

    public AppResponse<String> resendCode(@Valid EmailResendDTO dto) {
        String code = RandomUtil.generateRandomCode();

        Optional<ProfileEntity> byEmail = profileRepository.findByEmailAndVisibleTrue(dto.getEmail());
        if (byEmail.isEmpty()) {
            throw new BadException("Profile does not exist");
        }

        ProfileEntity entity = byEmail.get();
        if (!entity.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            throw new BadException("Verification failed");
        }

        emailSendService.sendHtmlEmail(entity.getEmail(), entity.getName(), code, EmailType.RESEND_CODE);
        return new AppResponse<>("Verification code resent");
    }

    public ProfileResponseDTO login(@Valid AuthDTO dto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            if (authenticate.isAuthenticated()) {
                CustomUserDetails principal = (CustomUserDetails) authenticate.getPrincipal();

                return getLoginResponse(principal.getId(), principal.getEmail(), principal.getName(), principal.getPhotoId(), principal.getCreatedDate());
            }
        } catch (Exception e) {
            throw new BadException("Invalid email or password");
        }
        throw new BadException("Invalid email or password");
    }

    public AppResponse<String> resetPassword(@Valid ResetPasswordDTO dto) {
        String code = RandomUtil.generateRandomCode();

        Optional<ProfileEntity> byEmail = profileRepository.findByEmailAndVisibleTrue(dto.getEmail());
        if (byEmail.isEmpty()) {
            throw new BadException("Profile does not exist");
        }
        ProfileEntity entity = byEmail.get();
        if (!entity.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new BadException("Wrong status");
        }

        if (EmailUtil.isEmail(dto.getEmail())) {
            emailSendService.sendHtmlEmail(dto.getEmail(), entity.getName(), code, EmailType.RESET_PASSWORD);
            return new AppResponse<>("Password reset email sent");
        }
        throw new BadException("Wrong email or password");
    }

    public AppResponse<String> resetPasswordConfirm(@Valid ResetPasswordConfirmDTO dto) {
        Optional<ProfileEntity> byEmail = profileRepository.findByEmailAndVisibleTrue(dto.getEmail());
        if (byEmail.isEmpty()) {
            throw new BadException("Profile does not exist");
        }
        ProfileEntity entity = byEmail.get();
        if (!entity.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new BadException("Wrong status");
        }

        if (EmailUtil.isEmail(dto.getEmail())) {
            emailHistoryService.checkCode(dto.getEmail(), dto.getCode());
        } else {
            throw new BadException("Wrong email or password");
        }

        // update password
        profileRepository.updatePasswordById(entity.getId(), passwordEncoder.encode(dto.getNewPassword()));
        return new AppResponse<>("Password changed successfully");
    }

    private ProfileResponseDTO getLoginResponse(UUID profileId, String email, String name, String photoId, LocalDateTime createdDate) {
        List<ProfileRoleEntity> allRoleByProfileId = profileRoleRepository.findAllByProfileId(profileId);
        List<Role> roleList = allRoleByProfileId.stream()
                .map(ProfileRoleEntity::getRole)
                .toList();

        // build response
        ProfileResponseDTO responseDTO = new ProfileResponseDTO();
        responseDTO.setName(name);
        responseDTO.setEmail(email);
        responseDTO.setRoles(roleList);
        responseDTO.setAccessToken(JwtUtil.encode(email, profileId, roleList));
        responseDTO.setCreatedDate(createdDate);
        responseDTO.setPhotoId(attachService.getPhoto(photoId));
        return responseDTO;
    }
}
