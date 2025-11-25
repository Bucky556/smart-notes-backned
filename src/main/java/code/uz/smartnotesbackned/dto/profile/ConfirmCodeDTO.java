package code.uz.smartnotesbackned.dto.profile;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmCodeDTO {
    @NotBlank(message = "Code required")
    private String code;
}
