package code.uz.smartnotesbackned.dto.profile;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePswdDTO {
    @NotBlank(message = "Current Password required")
    private String currentPassword;
    @NotBlank(message = "New Password required")
    private String newPassword;
}
