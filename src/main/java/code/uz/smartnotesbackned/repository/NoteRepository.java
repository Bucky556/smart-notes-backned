package code.uz.smartnotesbackned.repository;

import code.uz.smartnotesbackned.entity.NoteEntity;
import code.uz.smartnotesbackned.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public interface NoteRepository extends JpaRepository<NoteEntity, Integer> {
    Page<NoteEntity> findAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(UUID profileId, Pageable pageable);
    Optional<NoteEntity> findByIdAndVisibleTrue(Integer id);

    @Modifying
    @Query("update NoteEntity set visible = false where id = :id")
    void changeVisibleById(Integer id);

    Page<NoteEntity> findAllByProfileIdAndVisibleTrueAndFavoriteTrueOrderByCreatedDateDesc(UUID profileId, Pageable pageable);

    @Modifying
    @Query("update NoteEntity set favorite = :favorite where id = :id")
    void changeFavoriteById(Integer id, Boolean favorite);

    @Query("from NoteEntity n where n.visible = true and n.reminderDate <= :now and n.notified = false")
    List<NoteEntity> findAllByReminderDateBeforeNow(@Param("now") LocalDateTime now);
}
