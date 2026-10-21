//package com.hamsoo.bpm.controller.notificationapiaddress;
//
//import com.hamsoo.bpm.service.api.notificationapiaddress.NotificationApiAddressService;
//import com.hamsoo.bpm.model.entity.notificationapiaddress.NotificationApiAddress;
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
//@Api(value = "NotificationApiAddress", description = "Rest full controller of NotificationApiAddress!")
//@RequestMapping("/api")
//public class NotificationApiAddressController {
//
//    @Autowired
//    private NotificationApiAddressService notificationApiAddressService;
//
//    //----------get by id---------
//    @ApiOperation(value = "find a NotificationApiAddress by id", response = NotificationApiAddress.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping("/NotificationApiAddress/{id}")
//    public ResponseEntity<?> getNotificationApiAddressById(@PathVariable("id") Long id) throws ResourceNotFoundException, BusinessException {
//        NotificationApiAddress notificationApiAddress = notificationApiAddressService.find(id).orElse(null);
//        if (notificationApiAddress == null) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(notificationApiAddress, HttpStatus.OK);
//    }
//
//    //----------get All---------
//    @ApiOperation(value = "list of all NotificationApiAddress", response = NotificationApiAddress.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/NotificationApiAddress/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<List<?>> listAllNotificationApiAddress() throws ResourceNotFoundException, BusinessException {
//        List<NotificationApiAddress> notificationApiAddressList;
//        notificationApiAddressList = notificationApiAddressService.getList();
//        if (notificationApiAddressList.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(notificationApiAddressList, HttpStatus.OK);
//        }
//    }
//
//    //----------Save---------
//    @ApiOperation(value = "save a NotificationApiAddress", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "created"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/NotificationApiAddress", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveNotificationApiAddress(@RequestBody NotificationApiAddress notificationApiAddress) throws ResourceCanNotPersistsException, BusinessException {
//        int returnValue = notificationApiAddressService.save(notificationApiAddress);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Save All---------
//    @ApiOperation(value = "save all NotificationApiAddress", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/NotificationApiAddresses", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveAllNotificationApiAddress(@RequestBody List<NotificationApiAddress> notificationApiAddressList) throws BatchOperationException, BusinessException {
//
//        int returnValue = notificationApiAddressService.saveAll(notificationApiAddressList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------Update---------
//    @ApiOperation(value = "update a NotificationApiAddress", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/NotificationApiAddress")
//    public ResponseEntity<NotificationApiAddress> updateNotificationApiAddress(@RequestBody NotificationApiAddress notificationApiAddress) throws ResourceCanNotPersistsException, BusinessException {
//
//        int returnValue = notificationApiAddressService.update(notificationApiAddress);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Update All---------
//    @ApiOperation(value = "update all NotificationApiAddress", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/NotificationApiAddresses")
//    public ResponseEntity<Void> updateAllNotificationApiAddress(@RequestBody List<NotificationApiAddress> notificationApiAddressList) throws BatchOperationException, BusinessException {
//
//        int returnValue = notificationApiAddressService.updateAll(notificationApiAddressList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------delete by id---------
//    @ApiOperation(value = "delete a NotificationApiAddress by id", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 204, message = "No Content"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @DeleteMapping("/NotificationApiAddress/{id}")
//    public ResponseEntity<NotificationApiAddress> deleteNotificationApiAddress(
//            @PathVariable("id") Long id) throws ResourceNotFoundException, CanNotDeleteException, BusinessException {
//        NotificationApiAddress notificationApiAddress = notificationApiAddressService.find(id).orElse(null);
//        if (notificationApiAddress == null) {
//            throw new ResourceNotFoundException();
//        }
//        int returnValue = notificationApiAddressService.delete(id);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            throw new CanNotDeleteException();
//        }
//    }
//
//    //----------load grid---------
//    @ApiOperation(value = "load grid of NotificationApiAddress", response = NotificationApiAddress.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/NotificationApiAddress/grid",
//            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Page<?>> gridLoadOfNotificationApiAddress(
//            @RequestParam int page, @RequestParam int size,
//            @RequestParam(name = "filter", required = false) String search,
//            @RequestParam(name = "orderby", required = false) String orderby,
//            @RequestParam(name = "direction", required = false) String direction) throws ResourceNotFoundException, BusinessException {
//        Page<?> dataGrid = notificationApiAddressService.gridLoad(search, PageRequest.of(page, size), orderby, direction);
//        if (dataGrid.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(dataGrid, HttpStatus.OK);
//        }
//    }
//}