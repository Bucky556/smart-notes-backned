package code.uz.smartnotesbackned.entity;

import code.uz.smartnotesbackned.enums.EmailType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "email_history")
public class EmailHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email")
    private String email;
    @Column(name = "code")
    private String code;
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    @Column(name = "email_type")
    @Enumerated(EnumType.STRING)
    private EmailType emailType;
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;
}
