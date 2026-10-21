package com.hamsoo.bpm.service.api.person;

import com.hamsoo.bpm.model.dto.person.PersonDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.repository.jpa.person.PersonRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;


public interface PersonService extends BaseService<Person, Long, PersonRepository> {
    PersonDto mapToDto(Person person);

    Person findByIdAndOrganOrElseThrow(Long personId, Long OrganId);

    Person findByIdOrElseThrow(Long personId);

    void passive(Long id);


    @Transactional(rollbackFor = Exception.class)
    void passivePersonFromOrgan(Long id);

    Person create(PersonDto personDto);

    Person findByNationalCode(String nationalCode);

    SearchDto list(PersonDto personDto, int page, int size, String orderBy, Sort.Direction direction);
}
