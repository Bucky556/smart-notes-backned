package code.uz.smartnotesbackned.dto.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDTO {
    @NotBlank(message = "Username required")
    private String email;
    @NotBlank(message = "Password required")
    private String password;
}
