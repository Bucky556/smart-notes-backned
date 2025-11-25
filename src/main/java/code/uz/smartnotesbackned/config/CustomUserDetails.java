package code.uz.smartnotesbackned.config;

import code.uz.smartnotesbackned.entity.ProfileEntity;
import code.uz.smartnotesbackned.entity.ProfileRoleEntity;
import code.uz.smartnotesbackned.enums.GeneralStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {
    private final UUID id;
    private final String name;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> roles;
    private final GeneralStatus status;
    private final String photoId;
    private final LocalDateTime createdDate;

    public CustomUserDetails(ProfileEntity profileEntity, List<ProfileRoleEntity> roles) {
        this.id = profileEntity.getId();
        this.name = profileEntity.getName();
        this.email = profileEntity.getEmail();
        this.password = profileEntity.getPassword();
        this.roles = roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole().name())).toList();
        this.status = profileEntity.getStatus();
        this.photoId = profileEntity.getPhotoId();
        this.createdDate = profileEntity.getCreatedDate();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(GeneralStatus.ACTIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
