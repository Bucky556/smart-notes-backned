package code.uz.smartnotesbackned.dto;

import code.uz.smartnotesbackned.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class JwtDTO {
    private UUID id;
    private String email;
    private List<Role> roles;
}
