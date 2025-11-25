package code.uz.smartnotesbackned.repository;

import code.uz.smartnotesbackned.entity.NotificationEntity;
import code.uz.smartnotesbackned.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {
    @Query("select n.note.id from NotificationEntity n")
    List<Long> findAllNotifiedNoteIds();

    List<NotificationEntity> findAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(UUID profileId);

    @Modifying
    @Query("update NoteEntity n set n.notified = true where n.id = :id")
    void markAsNotified(@Param("id") Integer id);

    Optional<NotificationEntity> findByIdAndProfileIdAndVisibleTrue(String id, UUID profileId);

    @Modifying
    @Query("update NotificationEntity n set n.notificationType = :type where n.id = :id and n.profile.id = :profileId")
    void updateStatusById(String id, NotificationType type, UUID profileId);

    @Modifying
    @Query("update NotificationEntity n set n.visible = false where n.id = :id and n.profile.id = :profileId")
    void softDelete(String id, UUID profileId);
}
