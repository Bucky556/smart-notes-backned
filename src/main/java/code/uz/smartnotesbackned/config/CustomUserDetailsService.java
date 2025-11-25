package code.uz.smartnotesbackned.config;

import code.uz.smartnotesbackned.entity.ProfileEntity;
import code.uz.smartnotesbackned.entity.ProfileRoleEntity;
import code.uz.smartnotesbackned.repository.ProfileRepository;
import code.uz.smartnotesbackned.repository.ProfileRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> profile = profileRepository.findByEmailAndVisibleTrue(username);
        if (profile.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }
        ProfileEntity profileEntity = profile.get();
        List<ProfileRoleEntity> roleList = profileRoleRepository.findAllByProfileId(profileEntity.getId());

        return new CustomUserDetails(profileEntity, roleList);
    }


}
