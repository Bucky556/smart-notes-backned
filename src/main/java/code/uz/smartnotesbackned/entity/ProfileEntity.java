package code.uz.smartnotesbackned.entity;

import code.uz.smartnotesbackned.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "profile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    @Column(name = "visible")
    private Boolean visible = Boolean.TRUE;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GeneralStatus status;
    @Column(name = "updated_username")
    private String updatedUsername;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)  // bunday qilsak console da query kam ketadi (performance ga yaxshi)
    private List<ProfileRoleEntity> role;

    @Column(name = "photo_id")
    private String photoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    private AttachEntity photo;
}
