package code.uz.smartnotesbackned.service;


import code.uz.smartnotesbackned.dto.AppResponse;
import code.uz.smartnotesbackned.dto.FilterDTO;
import code.uz.smartnotesbackned.dto.profile.*;
import code.uz.smartnotesbackned.entity.ProfileEntity;
import code.uz.smartnotesbackned.entity.ProfileRoleEntity;
import code.uz.smartnotesbackned.enums.EmailType;
import code.uz.smartnotesbackned.enums.GeneralStatus;
import code.uz.smartnotesbackned.enums.Role;
import code.uz.smartnotesbackned.exception.BadException;
import code.uz.smartnotesbackned.exception.NotFoundException;
import code.uz.smartnotesbackned.mapper.ProfileMapper;
import code.uz.smartnotesbackned.repository.ProfileRepository;
import code.uz.smartnotesbackned.repository.ProfileRoleRepository;
import code.uz.smartnotesbackned.util.EmailUtil;
import code.uz.smartnotesbackned.util.JwtUtil;
import code.uz.smartnotesbackned.util.RandomUtil;
import code.uz.smartnotesbackned.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendService emailSendService;
    private final EmailHistoryService emailHistoryService;
    private final ProfileRoleRepository profileRoleRepository;
    private final AttachService attachService;


    public void updateName(ProfileNameDTO dto) {
        UUID profileId = SecurityUtil.getID();
        profileRepository.updateNameById(profileId, dto.getName());
    }

    public void updatePassword(@Valid ProfilePswdDTO dto) {
        UUID profileId = SecurityUtil.getID();

        ProfileEntity profile = getEntity(profileId);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), profile.getPassword())) {
            throw new BadException("Current password wrong");
        }

        profileRepository.updatePasswordById(profileId, passwordEncoder.encode(dto.getNewPassword()));

    }

    public void requestEmailUpdate(@Valid ProfileUsernameDTO dto) {
        UUID profileId = SecurityUtil.getID();
        ProfileEntity profile = getEntity(profileId);


        if (profileRepository.existsByEmailAndVisibleTrueAndStatus(dto.getUsername(), GeneralStatus.ACTIVE)) {
            throw new BadException("This username already in use");
        }

        if (EmailUtil.isEmail(dto.getUsername())) {
            String code = RandomUtil.generateRandomCode();
            emailSendService.sendHtmlEmail(dto.getUsername(), profile.getName(), code, EmailType.UPDATE_EMAIL);
        }

        profileRepository.updateUpd_username(profileId, dto.getUsername());
    }

    public AppResponse<String> confirmCode(@Valid ConfirmCodeDTO dto) {
        UUID profileId = SecurityUtil.getID();
        ProfileEntity profile = getEntity(profileId);

        if (EmailUtil.isEmail(profile.getUpdatedUsername())) {
            emailHistoryService.checkCode(profile.getUpdatedUsername(), dto.getCode());
        }

        profileRepository.updateEmailById(profileId, profile.getUpdatedUsername());
        // response by changing token into new email
        List<ProfileRoleEntity> roles = profileRoleRepository.findAllByProfileId(profileId);
        List<Role> roleList = roles.stream()
                .map(ProfileRoleEntity::getRole)
                .toList();
        String newJwt = JwtUtil.encode(profile.getUpdatedUsername(), profile.getId(), roleList);
        return new AppResponse<>(newJwt, "Email updated successfully");
    }

    public AppResponse<String> updatePhoto(@Valid ProfileImageDTO dto) {
        UUID profileId = SecurityUtil.getID();
        ProfileEntity profile = getEntity(profileId);

        profileRepository.updatePhotoById(profileId, dto.getPhotoId());

        if (profile.getPhotoId() != null && profile.getPhotoId().equals(dto.getPhotoId())) {
            attachService.delete(profile.getPhotoId());
        }

        return new AppResponse<>("Photo successfully updated");
    }

    public AppResponse<String> changeStatus(GeneralStatus status, UUID profileID) {
        profileRepository.updateStatusById(status, profileID);
        return new AppResponse<>("Status changed to " + status);
    }

    public AppResponse<String> delete(UUID profileId) {
        profileRepository.changeVisibleById(profileId);
        return new AppResponse<>("Profile deleted");
    }

    public PageImpl<ProfileResponseDTO> filter(FilterDTO dto, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProfileMapper> profile = null;
        if (dto.getQuery() == null || dto.getQuery().isEmpty()) {
            profile = profileRepository.findAllProfile(pageRequest);
        } else {
            profile = profileRepository.findProfileWithQuery("%" + dto.getQuery().toLowerCase() + "%", pageRequest);
        }

        List<ProfileResponseDTO> resultList = profile.getContent()
                .stream()
                .map(this::toDTOMapper)
                .toList();

        return new PageImpl<>(resultList, pageRequest, profile.getTotalElements());
    }

    private ProfileEntity getEntity(UUID profileId) {
        return profileRepository.findByIdAndVisibleTrue(profileId).orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    private ProfileResponseDTO toDTOMapper(ProfileMapper profileMapper) {
        ProfileResponseDTO responseDTO = new ProfileResponseDTO();
        responseDTO.setId(profileMapper.getId());
        responseDTO.setName(profileMapper.getName());
        responseDTO.setEmail(profileMapper.getEmail());
        responseDTO.setStatus(profileMapper.getStatus());
        responseDTO.setCreatedDate(profileMapper.getCreatedDate());
        responseDTO.setPhotoId(attachService.getPhoto(profileMapper.getPhotoId()));
        if (profileMapper.getRoles() != null) {
            List<Role> roleList = Arrays.stream(profileMapper.getRoles().split(","))
                    .map(Role::valueOf)
                    .toList();
            responseDTO.setRoles(roleList);
        }
        return responseDTO;
    }
}
