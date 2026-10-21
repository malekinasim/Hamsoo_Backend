package com.hamsoo.bpm.repository.jpa.person;

import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends HamsooRepository<Person, Long> {
    @Query(value = "select p from Person p  " +
            " left join UserOrgan uo on uo.person.id=p.id" +
            " left join uo.organ o " +
            "  where " +
            "( uo.id is null or ( uo.status='ACTIVE' and " +
            " o.status='ACTIVE' and " +
            " o.id=:organId ) )and " +
            " p.status='ACTIVE' and " +
            " p.id=:personId")
    Optional<Person> getPersonByIdAndOrganId(@Param("personId") Long personId, @Param("organId") Long organId);

    @Query(value = "select p from Person p  " +
            " left join UserOrgan uo on uo.person.id=p.id" +
            " left join uo.organ o " +
            "  where " +
            " (uo.id is null or  (uo.status='ACTIVE' and " +
            " o.status='ACTIVE') ) and " +
            " p.status='ACTIVE' and " +
            " p.id=:personId")
    Optional<Person> getPersonById(@Param("personId") Long personId);

    Person findByNationalCode(String nationalCode);
}