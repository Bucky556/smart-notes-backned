package code.uz.smartnotesbackned.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "Name required")
    private String name;
    @NotBlank(message = "Email required")
    private String email;
    @NotBlank(message = "Password required")
    private String password;
    @NotBlank(message = "Confirm password required")
    private String confirmPassword;
}
