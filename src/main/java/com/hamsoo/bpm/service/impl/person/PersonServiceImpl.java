package com.hamsoo.bpm.service.impl.person;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.person.PersonDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.userorgan.UserOrganDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.person.Person_;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan;
import com.hamsoo.bpm.model.entity.userorgan.UserOrgan_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.person.PersonRepository;
import com.hamsoo.bpm.service.api.person.PersonService;
import com.hamsoo.bpm.service.api.postmember.PostMemberService;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final PostMemberService postMemberService;
    private final UserOrganService userOrganService;
    private final UserService userService;

    public PersonServiceImpl(PersonRepository personRepository, @Lazy PostMemberService postMemberService, UserOrganService userOrganService, UserService userService) {
        this.personRepository = personRepository;
        this.postMemberService = postMemberService;
        this.userOrganService = userOrganService;
        this.userService = userService;
    }

    @Override
    public PersonRepository getRepository() {
        return personRepository;
    }

    @Override
    public PersonDto mapToDto(Person person) {
        PersonDto personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setName(person.getName());
        personDto.setFamily(person.getFamily());
        personDto.setPersonalCode(person.getPersonalCode());
        personDto.setNationalCode(person.getNationalCode());
        return personDto;
    }

    @Override
    public Person findByIdAndOrganOrElseThrow(Long personId, Long OrganId) {
        return this.getRepository().getPersonByIdAndOrganId(personId, OrganId)
                .orElseThrow(() -> new BusinessException("error.person.personNotFound"));
    }

    @Override
    public Person findByIdOrElseThrow(Long personId) {
        return this.getRepository().getPersonById(personId)
                .orElseThrow(() -> new BusinessException("error.person.personNotFound"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passive(Long id) {
        Person person = this.findByIdOrElseThrow(id);
        List<PostMember> postMemberList = postMemberService.findAllByPersonIdAndAllOrgan(person.getId());
        postMemberList.forEach(postMember ->
                postMemberService.passive(postMember.getId())
        );

        List<UserOrgan> userOrganList = userOrganService.findAllByPersonId(person.getId());

        userOrganList.forEach(postMember ->
                userOrganService.passiveOrganMemberAndModelMemberOfOrgan(postMember.getId())
        );

        person.setStatus(Status.PASSIVE.name());
        this.save(person);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passivePersonFromOrgan(Long id) {
        Person person = this.findByIdOrElseThrow(id);
        List<PostMember> postMemberList = postMemberService.findAllByPersonIdAndAllOrgan(person.getId());
        postMemberList.forEach(postMember ->
                postMemberService.passive(postMember.getId())
        );

        List<UserOrgan> userOrganList = userOrganService.findAllByPersonId(person.getId());

        userOrganList.forEach(postMember ->
                userOrganService.passiveOrganMemberAndModelMemberOfOrgan(postMember.getId())
        );

    }

    @Override
    public Person create(PersonDto personDto) {
        Assert.notNull(personDto, "personDto is null");
        Person person = new Person();
        person.setName(personDto.getName());
        person.setFamily(personDto.getFamily());
        person.setStatus(Status.ACTIVE.name());
        person.setNationalCode(person.getNationalCode());
        person.setPersonalCode(person.getPersonalCode());
        person = save(person);
        Optional<User> user = userService.findByNationalCode(personDto.getNationalCode());
        if (user.isPresent()) {
            List<UserOrgan> userOrgans = userOrganService.findAllOrganOfUser(user.get()).stream().filter(
                    userOrgan -> userOrgan.getOrgan().getId().equals(SecurityUtils.getCurrentOrganIdOrElseThrow())
            ).toList();
            if (userOrgans.isEmpty()) {
                UserOrganDto userOrganDto = new UserOrganDto();
                userOrganDto.setOrganId(SecurityUtils.getCurrentOrganIdOrElseThrow());
                userOrganDto.setUserId(user.get().getId());
                userOrganDto.setPersonId(person.getId());
                userOrganService.create(userOrganDto);
            } else {
                Person finalPerson = person;
                userOrgans.forEach(userOrgan -> {
                    UserOrganDto userOrganDto = new UserOrganDto();
                    userOrganDto.setOrganId(SecurityUtils.getCurrentOrganIdOrElseThrow());
                    userOrganDto.setUserId(user.get().getId());
                    userOrganDto.setPersonId(finalPerson.getId());
                    userOrganService.edit(userOrganDto);
                });
            }

        }

        return person;
    }

    @Override
    public Person findByNationalCode(String nationalCode) {
        return this.getRepository().findByNationalCode(nationalCode);
    }

    @Override
    public SearchDto list(PersonDto personDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Person>> optionalSpecification = createSpecification(personDto);
        Page<Person> postPage = findAll(optionalSpecification.get(), pageable);
        List<PersonDto> content = postPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }


    private Optional<Specification<Person>> createSpecification(PersonDto personDto) {
        List<Specification<Person>> andSpecificationList = new ArrayList<>();

        var wrapper = new Object() {
            Join<Person, User> personUserJoin;

        };
        List<Specification<Person>> orSpecificationList = new ArrayList<>();

        Specification<Person> organEq = (root, query, criteriaBuilder) -> {


            wrapper.personUserJoin = root.joinList(Person_.USER_ORGANS);
            Join<UserOrgan, Organ> organJoin =  wrapper.personUserJoin.join(UserOrgan_.ORGAN);
            Join<Organ, Chart> chartJoin = organJoin.joinList(Organ_.CHARTS);
            Predicate[] andPredicates = new Predicate[5];


            andPredicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            andPredicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());

            andPredicates[2] = criteriaBuilder.equal(chartJoin.get(Chart_.ID), personDto.getChartId());
            andPredicates[3] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS), Status.ACTIVE.name());

            andPredicates[4] = criteriaBuilder.equal(root.get(Person_.STATUS), Status.ACTIVE.name());

            query.distinct(true);
            return criteriaBuilder.and(andPredicates);
        };
        andSpecificationList.add(organEq);
        if (StringUtils.hasText(personDto.getSearchWord())) {
            Specification<Person> searchWordLike = (root, query, criteriaBuilder) -> {
                List<Predicate> predicatesOr = new ArrayList<>();
                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Person_.FULL_NAME)),
                        "%" + personDto.getSearchWord().toLowerCase() + "%"));

                predicatesOr.add(criteriaBuilder.equal(root.get(Person_.NATIONAL_CODE),
                        "%" + personDto.getSearchWord().toLowerCase() + "%"));
                query.distinct(true);
                return criteriaBuilder.or(predicatesOr.toArray(new Predicate[0]));
            };
            andSpecificationList.add(searchWordLike);
        }


        orSpecificationList.add(SpecificationUtils.andSpecifications(andSpecificationList).get());


        return SpecificationUtils.orSpecifications(orSpecificationList);
    }

}
