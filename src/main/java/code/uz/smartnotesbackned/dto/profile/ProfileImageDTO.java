package code.uz.smartnotesbackned.dto.profile;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileImageDTO {
    @NotBlank(message = "Photo id required")
    private String photoId;
}
