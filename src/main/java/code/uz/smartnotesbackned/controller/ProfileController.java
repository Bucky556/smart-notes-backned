package code.uz.smartnotesbackned.controller;


import code.uz.smartnotesbackned.dto.AppResponse;
import code.uz.smartnotesbackned.dto.FilterDTO;
import code.uz.smartnotesbackned.dto.profile.*;
import code.uz.smartnotesbackned.service.ProfileService;
import code.uz.smartnotesbackned.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@EnableMethodSecurity
@Tag(
        name = "Profile Controller",
        description = "Manages user profile operations including name, password, photo updates, and admin-level profile management."
)
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    @Operation(
            summary = "Update profile name",
            description = "Allows a logged-in user to update their name."
    )
    @PutMapping("/update/name")
    public ResponseEntity<AppResponse<String>> updateName(@RequestBody @Valid ProfileNameDTO dto) {
        log.info("Profile name update attempt: newName={}", dto.getName());
        profileService.updateName(dto);
        return ResponseEntity.ok(new AppResponse<>("Name updated successfully"));
    }

    @Operation(
            summary = "Update profile password",
            description = "Allows a logged-in user to change their password after validation."
    )
    @PutMapping("/update/password")
    public ResponseEntity<AppResponse<String>> updatePassword(@RequestBody @Valid ProfilePswdDTO dto) {
        log.info("Password update attempt for current user");
        profileService.updatePassword(dto);
        return ResponseEntity.ok(new AppResponse<>("Password updated successfully"));
    }

    @Operation(
            summary = "Request username (email) update",
            description = "Sends a verification code to the new email address to confirm the username (email) change."
    )
    @PutMapping("/update/username")
    public ResponseEntity<AppResponse<String>> requestEmailUpdate(@RequestBody @Valid ProfileUsernameDTO dto) {
        log.info("Request to update email to: {}", dto.getUsername());
        profileService.requestEmailUpdate(dto);
        return ResponseEntity.ok(new AppResponse<>("Code sent to %s".formatted(dto.getUsername())));
    }

    @Operation(
            summary = "Confirm username (email) update",
            description = "Verifies the confirmation code sent to the user's new email and updates the username."
    )
    @PutMapping("/update/username/confirm")
    public ResponseEntity<AppResponse<String>> confirmEmailUpdate(@RequestBody @Valid ConfirmCodeDTO dto) {
        log.info("Confirm email update attempt with code: {}", dto.getCode());
        return ResponseEntity.ok(profileService.confirmCode(dto));
    }

    @Operation(
            summary = "Update profile photo",
            description = "Allows a logged-in user to update their profile picture using the photo ID."
    )
    @PutMapping("/update/photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@RequestBody @Valid ProfileImageDTO dto) {
        log.info("Profile photo update attempt with imageId: {}", dto.getPhotoId());
        return ResponseEntity.ok(profileService.updatePhoto(dto));
    }

    /// ================= ADMIN ENDPOINTS ================= ///

    @Operation(
            summary = "Change user status (Admin only)",
            description = "Allows an admin to activate or block a specific user's profile."
    )
    @PutMapping("/admin/change/status/{profileID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> changeStatus(@RequestBody ProfileStatusDTO dto, @PathVariable UUID profileID) {
        log.info("Admin changing status for profileId={} to {}", profileID, dto.getStatus());
        return ResponseEntity.ok(profileService.changeStatus(dto.getStatus(), profileID));
    }

    @Operation(
            summary = "Delete a user profile (Admin only)",
            description = "Allows an admin to permanently delete a user's profile by their unique ID."
    )
    @DeleteMapping("/admin/delete/{profileId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> deleteProfile(@PathVariable UUID profileId) {
        log.warn("Admin deleting profile with id={}", profileId);
        return ResponseEntity.ok(profileService.delete(profileId));
    }

    @Operation(
            summary = "Filter and search profiles (Admin only)",
            description = "Allows an admin to filter and paginate user profiles based on query (name, email)"
    )
    @PostMapping("/admin/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<ProfileResponseDTO>> filter(
            @RequestBody FilterDTO dto,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        log.info("Admin filtering profiles with query: {}", dto.getQuery());
        return ResponseEntity.ok(profileService.filter(dto, PageUtil.getPage(page), size));
    }
}
