package code.uz.smartnotesbackned.dto.note;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoteCreateDTO {
    @NotBlank(message = "title required")
    private String title;
    @NotBlank(message = "content required")
    private String content;
    private LocalDateTime reminderDate;
    private Boolean favorite;
}
