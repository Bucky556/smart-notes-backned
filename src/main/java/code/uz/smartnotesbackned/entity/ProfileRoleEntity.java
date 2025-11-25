package code.uz.smartnotesbackned.entity;

import code.uz.smartnotesbackned.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "profile_role")
public class ProfileRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "profile_id")
    private UUID profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;
}
