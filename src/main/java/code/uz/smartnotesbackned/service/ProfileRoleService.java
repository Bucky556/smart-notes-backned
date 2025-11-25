package code.uz.smartnotesbackned.service;

import code.uz.smartnotesbackned.entity.ProfileRoleEntity;
import code.uz.smartnotesbackned.enums.Role;
import code.uz.smartnotesbackned.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileRoleService {
    private final ProfileRoleRepository profileRoleRepository;

    public void deleteRoles(UUID profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }

    public void create(UUID id, List<Role> roles) {
        List<ProfileRoleEntity> roleEntities = roles.stream()
                .map(role -> {
                    ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
                    profileRoleEntity.setProfileId(id);
                    profileRoleEntity.setRole(role);
                    return profileRoleEntity;
                }).toList();
        profileRoleRepository.saveAll(roleEntities);
    }
}
