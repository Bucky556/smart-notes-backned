package code.uz.smartnotesbackned.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "note")
@Getter
@Setter
public class NoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;
    @Column(name = "favorite")
    private Boolean favorite = false;
    @Column(name = "visible")
    private Boolean visible = true;
    @Column(name = "notified")
    private Boolean notified = false;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;
}
