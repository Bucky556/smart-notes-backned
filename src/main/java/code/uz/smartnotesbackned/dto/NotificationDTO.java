package code.uz.smartnotesbackned.dto;


import code.uz.smartnotesbackned.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {
    private String id;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime sentTime;
    private Integer noteId;
}
