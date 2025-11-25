package code.uz.smartnotesbackned.service;

import code.uz.smartnotesbackned.dto.AppResponse;
import code.uz.smartnotesbackned.dto.NotificationDTO;
import code.uz.smartnotesbackned.entity.NoteEntity;
import code.uz.smartnotesbackned.entity.NotificationEntity;
import code.uz.smartnotesbackned.enums.NotificationType;
import code.uz.smartnotesbackned.exception.NotFoundException;
import code.uz.smartnotesbackned.repository.NoteRepository;
import code.uz.smartnotesbackned.repository.NotificationRepository;
import code.uz.smartnotesbackned.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NoteRepository noteRepository;
    private final EmailSendService emailSendService;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedDelay = 20000) // runs every 20 seconds
    @Transactional
    public void sendNotification() {
        LocalDateTime now = LocalDateTime.now();

        List<NoteEntity> notesByReminder = noteRepository.findAllByReminderDateBeforeNow(now);
        List<Long> allNotifiedNoteIds = notificationRepository.findAllNotifiedNoteIds();
        if (notesByReminder.isEmpty()) return;

        for (NoteEntity note : notesByReminder) {
            if (allNotifiedNoteIds.contains(note.getId())) continue;


            NotificationEntity entity = new NotificationEntity();
            entity.setTitle("Reminder: " + note.getTitle());
            entity.setMessage("Message: " + note.getContent());
            entity.setNotificationType(NotificationType.UNREAD);
            entity.setProfile(note.getProfile());
            entity.setNote(note);
            notificationRepository.save(entity);

            emailSendService.sendNotificationEmail(note.getProfile().getEmail(), note.getProfile().getName(), entity.getTitle(), entity.getMessage());

            notificationRepository.markAsNotified(note.getId());
        }
    }

    public List<NotificationDTO> getAll() {
        UUID profileId = SecurityUtil.getID();

        List<NotificationEntity> byProfileId = notificationRepository.findAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(profileId);

        return byProfileId.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO getById(String id) {
        UUID profileId = SecurityUtil.getID();

        Optional<NotificationEntity> byIdToProfile = notificationRepository.findByIdAndProfileIdAndVisibleTrue(id, profileId);
        if (byIdToProfile.isEmpty()) {
            throw new NotFoundException("Notification not found");
        }

        if (byIdToProfile.get().getNotificationType() == NotificationType.UNREAD) {
            notificationRepository.updateStatusById(id, NotificationType.READ, profileId);
        }
        return toAllDTO(byIdToProfile.get());
    }

    public AppResponse<String> delete(String id) {
        UUID profileId = SecurityUtil.getID();

        Optional<NotificationEntity> byId = notificationRepository.findByIdAndProfileIdAndVisibleTrue(id, profileId);
        if (byId.isEmpty()) throw new NotFoundException("Notification not found");

        notificationRepository.softDelete(id, profileId);

        return new AppResponse<>("Notification deleted");
    }

    private NotificationDTO toAllDTO(NotificationEntity notificationEntity) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notificationEntity.getId());
        notificationDTO.setTitle(notificationEntity.getTitle());
        notificationDTO.setMessage(notificationEntity.getMessage());
        notificationDTO.setSentTime(notificationEntity.getCreatedDate());
        notificationDTO.setType(NotificationType.READ);
        notificationDTO.setNoteId(notificationEntity.getNote().getId());
        return notificationDTO;
    }

    private NotificationDTO toDTO(NotificationEntity notificationEntity) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notificationEntity.getId());
        notificationDTO.setTitle(notificationEntity.getTitle());
        notificationDTO.setType(notificationEntity.getNotificationType());
        notificationDTO.setNoteId(notificationEntity.getNote().getId());
        return notificationDTO;
    }
}
