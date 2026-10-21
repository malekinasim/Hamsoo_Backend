//package com.hamsoo.bpm.controller.notificationaudience;
//
//import com.hamsoo.bpm.service.api.notificationaudience.NotificationAudienceService;
//import com.hamsoo.bpm.model.entity.notificationaudience.NotificationAudience;
//
//import com.hamsoo.bpm.common.exception.*;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@Api(value = "NotificationAudience", description = "Rest full controller of NotificationAudience!")
//@RequestMapping("/api")
//public class NotificationAudienceController {
//
//    @Autowired
//    private NotificationAudienceService notificationAudienceService;
//
//    //----------get by id---------
//    @ApiOperation(value = "find a NotificationAudience by id", response = NotificationAudience.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping("/NotificationAudience/{id}")
//    public ResponseEntity<?> getNotificationAudienceById(@PathVariable("id") Long id) throws ResourceNotFoundException, BusinessException {
//        NotificationAudience notificationAudience = notificationAudienceService.find(id).orElse(null);
//        if (notificationAudience == null) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(notificationAudience, HttpStatus.OK);
//    }
//
//    //----------get All---------
//    @ApiOperation(value = "list of all NotificationAudience", response = NotificationAudience.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/NotificationAudience/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<List<?>> listAllNotificationAudience() throws ResourceNotFoundException, BusinessException {
//        List<NotificationAudience> notificationAudienceList;
//        notificationAudienceList = notificationAudienceService.getList();
//        if (notificationAudienceList.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(notificationAudienceList, HttpStatus.OK);
//        }
//    }
//
//    //----------Save---------
//    @ApiOperation(value = "save a NotificationAudience", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "created"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/NotificationAudience", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveNotificationAudience(@RequestBody NotificationAudience notificationAudience) throws ResourceCanNotPersistsException, BusinessException {
//        int returnValue = notificationAudienceService.save(notificationAudience);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Save All---------
//    @ApiOperation(value = "save all NotificationAudience", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/NotificationAudiences", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveAllNotificationAudience(@RequestBody List<NotificationAudience> notificationAudienceList) throws BatchOperationException, BusinessException {
//
//        int returnValue = notificationAudienceService.saveAll(notificationAudienceList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------Update---------
//    @ApiOperation(value = "update a NotificationAudience", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/NotificationAudience")
//    public ResponseEntity<NotificationAudience> updateNotificationAudience(@RequestBody NotificationAudience notificationAudience) throws ResourceCanNotPersistsException, BusinessException {
//
//        int returnValue = notificationAudienceService.update(notificationAudience);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Update All---------
//    @ApiOperation(value = "update all NotificationAudience", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/NotificationAudiences")
//    public ResponseEntity<Void> updateAllNotificationAudience(@RequestBody List<NotificationAudience> notificationAudienceList) throws BatchOperationException, BusinessException {
//
//        int returnValue = notificationAudienceService.updateAll(notificationAudienceList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------delete by id---------
//    @ApiOperation(value = "delete a NotificationAudience by id", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 204, message = "No Content"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @DeleteMapping("/NotificationAudience/{id}")
//    public ResponseEntity<NotificationAudience> deleteNotificationAudience(
//            @PathVariable("id") Long id) throws ResourceNotFoundException, CanNotDeleteException, BusinessException {
//        NotificationAudience notificationAudience = notificationAudienceService.find(id).orElse(null);
//        if (notificationAudience == null) {
//            throw new ResourceNotFoundException();
//        }
//        int returnValue = notificationAudienceService.delete(id);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            throw new CanNotDeleteException();
//        }
//    }
//
//    //----------load grid---------
//    @ApiOperation(value = "load grid of NotificationAudience", response = NotificationAudience.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/NotificationAudience/grid",
//            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Page<?>> gridLoadOfNotificationAudience(
//            @RequestParam int page, @RequestParam int size,
//            @RequestParam(name = "filter", required = false) String search,
//            @RequestParam(name = "orderby", required = false) String orderby,
//            @RequestParam(name = "direction", required = false) String direction) throws ResourceNotFoundException, BusinessException {
//        Page<?> dataGrid = notificationAudienceService.gridLoad(search, PageRequest.of(page, size), orderby, direction);
//        if (dataGrid.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(dataGrid, HttpStatus.OK);
//        }
//    }
//}