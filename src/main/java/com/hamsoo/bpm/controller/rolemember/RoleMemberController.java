//package com.hamsoo.bpm.controller.rolemember;
//
//import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
//import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
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
//@Api(value = "RoleMember", description = "Rest full controller of RoleMember!")
//@RequestMapping("/api")
//public class RoleMemberController {
//
//    @Autowired
//    private RoleMemberService roleMemberService;
//
//    //----------get by id---------
//    @ApiOperation(value = "find a RoleMember by id", response = RoleMember.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping("/RoleMember/{id}")
//    public ResponseEntity<?> getRoleMemberById(@PathVariable("id") Long id) throws ResourceNotFoundException, BusinessException {
//        RoleMember roleMember = roleMemberService.find(id).orElse(null);
//        if (roleMember == null) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(roleMember, HttpStatus.OK);
//    }
//
//    //----------get All---------
//    @ApiOperation(value = "list of all RoleMember", response = RoleMember.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/RoleMember/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<List<?>> listAllRoleMember() throws ResourceNotFoundException, BusinessException {
//        List<RoleMember> roleMemberList;
//        roleMemberList = roleMemberService.getList();
//        if (roleMemberList.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(roleMemberList, HttpStatus.OK);
//        }
//    }
//
//    //----------Save---------
//    @ApiOperation(value = "save a RoleMember", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "created"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/RoleMember", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveRoleMember(@RequestBody RoleMember roleMember) throws ResourceCanNotPersistsException, BusinessException {
//        int returnValue = roleMemberService.save(roleMember);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Save All---------
//    @ApiOperation(value = "save all RoleMember", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PostMapping(value = "/RoleMembers", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> saveAllRoleMember(@RequestBody List<RoleMember> roleMemberList) throws BatchOperationException, BusinessException {
//
//        int returnValue = roleMemberService.saveAll(roleMemberList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------Update---------
//    @ApiOperation(value = "update a RoleMember", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/RoleMember")
//    public ResponseEntity<RoleMember> updateRoleMember(@RequestBody RoleMember roleMember) throws ResourceCanNotPersistsException, BusinessException {
//
//        int returnValue = roleMemberService.update(roleMember);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new ResourceCanNotPersistsException();
//        }
//    }
//
//    //----------Update All---------
//    @ApiOperation(value = "update all RoleMember", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @PutMapping("/RoleMembers")
//    public ResponseEntity<Void> updateAllRoleMember(@RequestBody List<RoleMember> roleMemberList) throws BatchOperationException, BusinessException {
//
//        int returnValue = roleMemberService.updateAll(roleMemberList);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            throw new BatchOperationException();
//        }
//    }
//
//    //----------delete by id---------
//    @ApiOperation(value = "delete a RoleMember by id", response = HttpStatus.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 204, message = "No Content"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @DeleteMapping("/RoleMember/{id}")
//    public ResponseEntity<RoleMember> deleteRoleMember(
//            @PathVariable("id") Long id) throws ResourceNotFoundException, CanNotDeleteException, BusinessException {
//        RoleMember roleMember = roleMemberService.find(id).orElse(null);
//        if (roleMember == null) {
//            throw new ResourceNotFoundException();
//        }
//        int returnValue = roleMemberService.delete(id);
//        if (returnValue == 1) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            throw new CanNotDeleteException();
//        }
//    }
//
//    //----------load grid---------
//    @ApiOperation(value = "load grid of RoleMember", response = RoleMember.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Ok"),
//            @ApiResponse(code = 417, message = "Expectation Failed")})
//    @GetMapping(value = "/RoleMember/grid",
//            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Page<?>> gridLoadOfRoleMember(
//            @RequestParam int page, @RequestParam int size,
//            @RequestParam(name = "filter", required = false) String search,
//            @RequestParam(name = "orderby", required = false) String orderby,
//            @RequestParam(name = "direction", required = false) String direction) throws ResourceNotFoundException, BusinessException {
//        Page<?> dataGrid = roleMemberService.gridLoad(search, PageRequest.of(page, size), orderby, direction);
//        if (dataGrid.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(dataGrid, HttpStatus.OK);
//        }
//    }
//}