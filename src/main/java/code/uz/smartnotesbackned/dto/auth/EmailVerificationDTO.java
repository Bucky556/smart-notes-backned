package code.uz.smartnotesbackned.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationDTO {
    @NotBlank(message = "Email required")
    private String email;
    @NotBlank(message = "Code required")
    private String code;

}
