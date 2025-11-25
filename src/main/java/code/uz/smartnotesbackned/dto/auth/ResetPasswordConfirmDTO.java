package code.uz.smartnotesbackned.dto.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmDTO {
    @NotBlank(message = "Email or Phone required")
    private String email;
    @NotBlank(message = "Code required")
    private String code;
    @NotBlank(message = "Password required")
    private String newPassword;
}
