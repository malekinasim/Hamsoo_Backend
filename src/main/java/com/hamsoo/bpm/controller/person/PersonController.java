package com.hamsoo.bpm.controller.person;


import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.model.dto.person.PersonDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.service.api.person.PersonService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")

public class PersonController {


    private final PersonService personService;


    public PersonController(PersonService personService) {
        this.personService = personService;
    }


    @GetMapping("/person/{id}")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_PERSON_MNG)")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Person person = personService.findByIdOrElseThrow(id);
        PersonDto personDto = personService.mapToDto(person);
        return new ResponseEntity<>(personDto, HttpStatus.OK);
    }

    @PostMapping("/person")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_EDIT)")
    public ResponseEntity<?> create(@Valid @RequestBody PersonDto personDto) {
        Person person = personService.create(personDto);
        personDto = personService.mapToDto(person);
        return new ResponseEntity<>(personDto, HttpStatus.OK);
    }

    @PostMapping(value = "/person/page")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_PERSON_MNG)")
    public ResponseEntity<?> pages(@RequestBody PersonDto personDto,
                                   @RequestParam int page,
                                   @RequestParam int size,
                                   @RequestParam(required = false) String orderBy,
                                   @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = personService.list(personDto, page, size, orderBy, direction);
        return new ResponseEntity<>(searchDto, HttpStatus.OK);
    }

    @PostMapping(value = "/person/list")
    @PreAuthorize(value = "hasAnyAuthority(@resourceItemService.ORGANIZATIONAL_CHART_PERSON_MNG)")
    public ResponseEntity<?> list(@RequestBody PersonDto personDto,
                                  @RequestParam(required = false) String orderBy,
                                  @RequestParam(required = false) Sort.Direction direction) {
        SearchDto searchDto = personService.list(personDto, 0, PageUtils.MAX_PAGE_SIZE, orderBy, direction);
        return new ResponseEntity<>(searchDto.getContent(), HttpStatus.OK);
    }
}
