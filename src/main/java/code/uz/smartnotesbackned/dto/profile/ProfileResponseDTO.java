package code.uz.smartnotesbackned.dto.profile;


import code.uz.smartnotesbackned.dto.AttachDTO;
import code.uz.smartnotesbackned.enums.GeneralStatus;
import code.uz.smartnotesbackned.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private GeneralStatus status;
    private LocalDateTime createdDate;
    private List<Role> roles;
    private String accessToken;
    private AttachDTO photoId;
}
