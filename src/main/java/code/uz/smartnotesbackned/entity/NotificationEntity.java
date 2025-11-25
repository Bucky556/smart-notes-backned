package code.uz.smartnotesbackned.entity;


import code.uz.smartnotesbackned.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "message")
    private String message;
    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    @Column(name = "visible")
    private Boolean visible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private NoteEntity note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

}
