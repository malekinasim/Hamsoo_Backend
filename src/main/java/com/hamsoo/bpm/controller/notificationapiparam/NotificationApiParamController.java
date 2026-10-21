//package com.hamsoo.bpm.controller.notificationapiparam;
//
//import com.hamsoo.bpm.service.api.notificationapiparam.NotificationApiParamService;
//import com.hamsoo.bpm.model.entity.notificationapiparam.NotificationApiParam;
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
//@Api(value = "NotificationApiParam", description = "Rest full controller of NotificationApiParam!")
//@RequestMapping("/api")
//public class NotificationApiParamController {
//
//    @Autowired
//    private NotificationApiParamService notificationApiParamService;
//
//    //----------get by id---------
//    @ApiOperation(value = "find a NotificationApiParam by id", response = NotificationApiParam.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping("/NotificationApiParam/{id}")
//    public ResponseEntity<?> getNotificationApiParamById(@PathVariable("id") Long id) throws ResourceNotFoundException, BusinessException {
//        NotificationApiParam notificationApiParam = notificationApiParamService.find(id).orElse(null);
//        if (notificationApiParam == null) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(notificationApiParam, HttpStatus.OK);
//    }
//
//    //----------get All---------
//    @ApiOperation(value = "list of all NotificationApiParam", response = NotificationApiParam.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/NotificationApiParam/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<List<?>> listAllNotificationApiParam() throws ResourceNotFoundException, BusinessException {
//        List<NotificationApiParam> notificationApiParamList;
//        notificationApiParamList = notificationApiParamService.getList();
//        if (notificationApiParamList.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(notificationApiParamList, HttpStatus.OK);
//        }
//    }
//
//    //----------Save---------
//    @ApiOperation(value = "save a NotificationApiParam", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "created"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/NotificationApiParam", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveNotificationApiParam(@RequestBody NotificationApiParam notificationApiParam) throws ResourceCanNotPersistsException, BusinessException {
//        int returnValue = notificationApiParamService.save(notificationApiParam);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Save All---------
//    @ApiOperation(value = "save all NotificationApiParam", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/NotificationApiParams", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveAllNotificationApiParam(@RequestBody List<NotificationApiParam> notificationApiParamList) throws BatchOperationException, BusinessException {
//
//        int returnValue = notificationApiParamService.saveAll(notificationApiParamList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------Update---------
//    @ApiOperation(value = "update a NotificationApiParam", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/NotificationApiParam")
//    public ResponseEntity<NotificationApiParam> updateNotificationApiParam(@RequestBody NotificationApiParam notificationApiParam) throws ResourceCanNotPersistsException, BusinessException {
//
//        int returnValue = notificationApiParamService.update(notificationApiParam);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Update All---------
//    @ApiOperation(value = "update all NotificationApiParam", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/NotificationApiParams")
//    public ResponseEntity<Void> updateAllNotificationApiParam(@RequestBody List<NotificationApiParam> notificationApiParamList) throws BatchOperationException, BusinessException {
//
//        int returnValue = notificationApiParamService.updateAll(notificationApiParamList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------delete by id---------
//    @ApiOperation(value = "delete a NotificationApiParam by id", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 204, message = "No Content"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @DeleteMapping("/NotificationApiParam/{id}")
//    public ResponseEntity<NotificationApiParam> deleteNotificationApiParam(
//            @PathVariable("id") Long id) throws ResourceNotFoundException, CanNotDeleteException, BusinessException {
//        NotificationApiParam notificationApiParam = notificationApiParamService.find(id).orElse(null);
//        if (notificationApiParam == null) {
//            throw new ResourceNotFoundException();
//        }
//        int returnValue = notificationApiParamService.delete(id);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            throw new CanNotDeleteException();
//        }
//    }
//
//    //----------load grid---------
//    @ApiOperation(value = "load grid of NotificationApiParam", response = NotificationApiParam.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/NotificationApiParam/grid",
//            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Page<?>> gridLoadOfNotificationApiParam(
//            @RequestParam int page, @RequestParam int size,
//            @RequestParam(name = "filter", required = false) String search,
//            @RequestParam(name = "orderby", required = false) String orderby,
//            @RequestParam(name = "direction", required = false) String direction) throws ResourceNotFoundException, BusinessException {
//        Page<?> dataGrid = notificationApiParamService.gridLoad(search, PageRequest.of(page, size), orderby, direction);
//        if (dataGrid.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(dataGrid, HttpStatus.OK);
//        }
//    }
//}