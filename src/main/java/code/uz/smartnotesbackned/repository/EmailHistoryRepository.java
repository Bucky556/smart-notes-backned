package code.uz.smartnotesbackned.repository;

import code.uz.smartnotesbackned.entity.EmailHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
public interface EmailHistoryRepository extends JpaRepository<EmailHistoryEntity, Integer> {
    Long countByEmailAndSendTimeBetween(String email, LocalDateTime from , LocalDateTime to);

    Optional<EmailHistoryEntity> findTop1ByEmailOrderBySendTimeDesc(String email);

    @Modifying
    @Query("update EmailHistoryEntity e set e.attemptCount = coalesce(e.attemptCount, 0) + 1 where e.email = :email")
    void updateAttemptCountByEmail(String email);
}
