package code.uz.smartnotesbackned.controller;


import code.uz.smartnotesbackned.dto.AppResponse;
import code.uz.smartnotesbackned.dto.NotificationDTO;
import code.uz.smartnotesbackned.service.NotificationService;
import code.uz.smartnotesbackned.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Tag(
        name = "Notification Controller",
        description = "Manages user notifications — sending, retrieving, and deleting notifications."
)
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(
            summary = "Send a notification",
            description = "Sends notification message about expiring reminder date in both email and system"
    )
    @PostMapping("/send")
    public ResponseEntity<AppResponse<String>> sendNotification() {
        String username = SecurityUtil.getUsername();
        log.info("Notification sent successfully by '{}'.", username);
        notificationService.sendNotification();
        return ResponseEntity.ok().body(new AppResponse<>("You have a notification"));
    }

    @Operation(
            summary = "Get all notifications",
            description = "Retrieves a list of all notifications available for the authenticated user."
    )
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        String username = SecurityUtil.getUsername();
        log.info("Fetched notifications for user '{}'.", username);
        return ResponseEntity.ok(notificationService.getAll());
    }

    @Operation(
            summary = "Get notification by ID",
            description = "Retrieves notification by ID for the authenticated user."
    )
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable String id) {
        String username = SecurityUtil.getUsername();
        log.info("Notification with ID={} successfully retrieved by '{}'.", id, username);
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @Operation(
            summary = "Delete notification by ID",
            description = "Deletes notification by changing visible to false without removing it from database"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<String>> deleteNotificationById(@PathVariable String id) {
        String username = SecurityUtil.getUsername();
        log.info("Notification ID={} deleted successfully by '{}'.", id, username);
        return ResponseEntity.ok(notificationService.delete(id));
    }
}
