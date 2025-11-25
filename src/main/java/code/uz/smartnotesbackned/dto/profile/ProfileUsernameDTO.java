package code.uz.smartnotesbackned.dto.profile;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUsernameDTO {
    @NotBlank(message = "Username required")
    private String username;
}
