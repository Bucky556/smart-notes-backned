package code.uz.smartnotesbackned.dto.note;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteDTO {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime reminderDate;
    private Boolean favorite;
}
