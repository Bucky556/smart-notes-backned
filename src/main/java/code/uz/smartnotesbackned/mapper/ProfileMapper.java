package code.uz.smartnotesbackned.mapper;

import code.uz.smartnotesbackned.enums.GeneralStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProfileMapper {
    UUID getId();
    String getName();
    String getEmail();
    LocalDateTime getCreatedDate();
    GeneralStatus getStatus();
    String getPhotoId();
    String getRoles();
}
