package code.uz.smartnotesbackned.dto.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailResendDTO {
    @NotBlank(message = "Email required")
    private String email;
}
