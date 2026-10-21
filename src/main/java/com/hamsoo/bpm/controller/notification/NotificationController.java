package com.hamsoo.bpm.controller.notification;

import com.hamsoo.bpm.model.dto.notification.NotificationDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.notification.Notification;
import com.hamsoo.bpm.service.api.notification.NotificationService;
import com.hamsoo.bpm.service.api.notificationaudience.NotificationAudienceService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationAudienceService notificationAudienceService;

    public NotificationController(NotificationService notificationService, NotificationAudienceService notificationAudienceService) {
        this.notificationService = notificationService;
        this.notificationAudienceService = notificationAudienceService;
    }

    @GetMapping("/notifications/{id}")
    public ResponseEntity<?> findNotificationById(@PathVariable("id") Long id) {
        Notification notification = notificationService.findByIdOrElseThrow(id);
        NotificationDto notificationDto = notificationService.mapToDto(notification);
        return new ResponseEntity<>(notificationDto, HttpStatus.OK);
    }

    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<?> passiveNotification(@PathVariable Long id) {
        notificationService.passive(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/notifications/current-user")
    public ResponseEntity<?> notificationOfCurrentUser(@RequestParam int page, @RequestParam int size,
                                                       @RequestParam(required = false) String orderBy,
                                                       @RequestParam(required = false) Sort.Direction direction, @RequestParam(required = false) Boolean readStatus) {
        SearchDto searchDto = notificationService.notificationOfCurrentUser(page, size, orderBy, direction, readStatus);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @GetMapping(value = "/model-versions/{modelVersionId}/notifications")
   @PreAuthorize("hasAnyResourceByModelVersionId(#modelVersionId,@resourceItemService.MODEL_INFO)")
    public ResponseEntity<?> getNotificationList(@PathVariable Long modelVersionId,
                                                 @RequestParam int page, @RequestParam int size,
                                                 @RequestParam(required = false) String orderBy,
                                                 @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = notificationService.notificationByModelVersion(modelVersionId, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

   @PostMapping("/notifications/update-read-status/{notificationAudienceId}")
    public ResponseEntity<?> updateNotificationReadStatus(@PathVariable Long notificationAudienceId) {
        notificationAudienceService.updateReadStatus(notificationAudienceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
