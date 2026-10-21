package com.hamsoo.bpm.service.impl.postmember;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.postmember.PostMemberDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.job.Job_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.person.Person;
import com.hamsoo.bpm.model.entity.person.Person_;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.post.Post_;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.postmember.PostMember_;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.unit.Unit_;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory;
import com.hamsoo.bpm.model.entity.unitcodehistory.UnitCodeHistory_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.postmember.PostMemberRepository;
import com.hamsoo.bpm.service.api.person.PersonService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.postmember.PostMemberService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostMemberServiceImpl implements PostMemberService {
    private final PostMemberRepository postMemberRepository;
    private final PostService postService;
    private final PersonService personService;

    public PostMemberServiceImpl(PostMemberRepository postMemberRepository, PostService postService, PersonService personService) {
        this.postMemberRepository = postMemberRepository;
        this.postService = postService;
        this.personService = personService;
    }

    @Override
    public PostMemberRepository getRepository() {
        return postMemberRepository;
    }

    @Override
    public PostMember findByIdOrElseThrow(Long id) {
        return getRepository().findPostMemberByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(
                () -> new BusinessException("error.postMember.idIsNotValid")
        );
    }

    @Override
    public PostMemberDto mapToDto(PostMember postMember) {
        PostMemberDto postMemberDto = new PostMemberDto();
        postMemberDto.setId(postMember.getId());
        postMemberDto.setPostId(postMember.getPost().getId());
        postMemberDto.setUnitId(postMember.getPost().getUnit().getId());
        postMemberDto.setJobId(postMember.getPost().getJob().getId());
        postMemberDto.setPersonId(postMember.getPerson().getId());

        postMemberDto.setPostName(postMember.getPost().getName());
        postMemberDto.setPersonFullName(postMember.getPerson().getName() + " " + postMember.getPerson().getFamily());
        Person _person = new Person();
        _person.setPersonalCode(postMember.getPerson().getPersonalCode());
        _person.setNationalCode(postMember.getPerson().getNationalCode());
        _person.setFullName(postMember.getPerson().getFullName());
        postMemberDto.setPersonInfo(_person);
        postMemberDto.setJobName(postMember.getPost().getJob().getName());
        postMemberDto.setUnitName(postMember.getPost().getUnit().getName());
        return postMemberDto;
    }

    @Override
    public PostMember create(PostMemberDto postMemberDto) {
        PostMember postMember = new PostMember();
        postMember.setPost(postService.findByIdOrElseThrow(postMemberDto.getPostId()));
        postMember.setPerson(personService.findByIdAndOrganOrElseThrow(postMemberDto.getPersonId(), SecurityUtils.getCurrentOrganIdOrElseThrow()));
        postMember.setStatus(Status.ACTIVE.name());
        postMember.setValidFrom(LocalDateTime.now());
        return this.save(postMember);

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void passive(Long id) {
        PostMember postMember = this.findByIdOrElseThrow(id);
        postMember.setValidTo(LocalDateTime.now());
        postMember.setStatus(Status.PASSIVE.name());
        this.save(postMember);


        //if person has not  another post in all organs  passive person in this Organ
        List<PostMember> postMemberList = this.findAllByPersonIdAndAllOrgan(postMember.getPerson().getId());
        if (postMemberList.isEmpty()) {
            personService.passivePersonFromOrgan(postMember.getPerson().getId());
        }

    }


    @Override
    public SearchDto list(PostMemberDto postMemberDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<PostMember>> optionalSpecification = createSpecification(postMemberDto);
        Page<PostMember> postPage = findAll(optionalSpecification.get(), pageable);
        List<PostMemberDto> content = postPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    @Override
    public Optional<PostMember> findByPersonIdAndPostIdAndOrganIdAndChartId(Long personId, Long postId, Long chartId) {
        return this.getRepository().findPostMemberByPersonIdAndPostIdAndOrganIdAndChartId(personId, postId, chartId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public List<PostMember> findAllByPersonIdAndOrganId(Long personId) {
        return this.getRepository().findAllPostMemberByPersonIdAndOrganId(personId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public List<PostMember> findAllByPersonIdAndAllOrgan(Long personId) {
        return this.getRepository().findAllByPersonIdAndAllOrgan(personId);

    }

    @Override
    public List<PostMember> findByPostIdAndOrganIdAndChartId(Long postId, Long chartId) {
        return this.getRepository().findAllByPostIdAndOrganId(postId, chartId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    private Optional<Specification<PostMember>> createSpecification(PostMemberDto postMemberDto) {

        List<Specification<PostMember>> specificationList = new ArrayList<>();
        Specification<PostMember> postMemberFilter = (root, query, criteriaBuilder) -> {
            Join<PostMember, Post> postJoin = root.join(PostMember_.POST);
            Join<Post, Unit> unitJoin = postJoin.join(Post_.UNIT);
            Join<Post, Job> jobJoin = postJoin.join(Post_.JOB);
            Join<Unit, Chart> chartJoin = unitJoin.join(Unit_.CHART);
            Join<Unit, UnitCodeHistory> unitCodeJoin = unitJoin.joinList(Unit_.UNIT_CODE_HISTORY_LIST);
            Join<Chart, Organ> organJoin = chartJoin.join(Chart_.ORGAN);
            Join<PostMember, Person> personJoin = root.join(PostMember_.PERSON);

            Predicate[] predicates = new Predicate[10];

            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());

            predicates[2] = criteriaBuilder.equal(chartJoin.get(Chart_.ID), postMemberDto.getChartId());
            predicates[3] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS), Status.ACTIVE.name());

            predicates[4] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS), Status.ACTIVE.name());

            predicates[5] = criteriaBuilder.equal(jobJoin.get(Job_.STATUS), Status.ACTIVE.name());

            predicates[6] = criteriaBuilder.equal(postJoin.get(Post_.STATUS), Status.ACTIVE.name());
            predicates[7] = criteriaBuilder.equal(root.get(PostMember_.STATUS), Status.ACTIVE.name());

            predicates[8] = criteriaBuilder.equal(personJoin.get(Person_.STATUS), Status.ACTIVE.name());
            predicates[9] = criteriaBuilder.equal(unitCodeJoin.get(UnitCodeHistory_.STATUS), Status.ACTIVE.name());


            if (StringUtils.hasText(postMemberDto.getSearchWord()) ) {
                List<Predicate> predicatesOr = new ArrayList<>();
                predicatesOr.add(personByFullNameOrCodeFilter(postMemberDto.getSearchWord(), personJoin, criteriaBuilder));
                predicatesOr.add(postNameOrCodeFilter(postMemberDto.getSearchWord(), postJoin, criteriaBuilder));
                predicatesOr.add(jobNameOrCodeFilter(postMemberDto.getSearchWord(), jobJoin, criteriaBuilder));
                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(unitJoin.get(Unit_.NAME)),
                        "%" + postMemberDto.getSearchWord().toLowerCase() + "%"));
                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(unitCodeJoin.get(UnitCodeHistory_.CODE)),
                        "%" + postMemberDto.getSearchWord().toLowerCase() + "%"));
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.or(predicatesOr.toArray(new Predicate[0])));

            }

            List<Predicate> predicatesNull = new ArrayList<>();
            if(postMemberDto.getPostId()!=null && postMemberDto.getPostId() != 0)
                predicatesNull.add(criteriaBuilder.equal(postJoin.get(Post_.id), postMemberDto.getPostId()));
            else
            {
                if (postMemberDto.getJobId() != null && postMemberDto.getJobId() != 0)
                    predicatesNull.add(criteriaBuilder.equal(jobJoin.get(Job_.id), postMemberDto.getJobId()));

                if (postMemberDto.getUnitId() != null && postMemberDto.getUnitId() != 0)
                    predicatesNull.add(criteriaBuilder.equal(unitJoin.get(Unit_.id), postMemberDto.getUnitId()));
            }

            if(predicatesNull.size()>0)
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.or(predicatesNull.toArray(new Predicate[0])));

            return criteriaBuilder.and(predicates);
        };
        specificationList.add(postMemberFilter);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    private Predicate personByFullNameOrCodeFilter(String searchWord, Join<PostMember, Person> personJoin, @NonNull CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicatesOr = new ArrayList<>();

        predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(personJoin.get(Person_.FULL_NAME)),
                "%" + searchWord.toLowerCase() + "%"));

        predicatesOr.add(criteriaBuilder.like(personJoin.get(Person_.PERSONAL_CODE),
                "%" + searchWord.toLowerCase() + "%"));

        predicatesOr.add(criteriaBuilder.like(personJoin.get(Person_.NATIONAL_CODE),
                "%" + searchWord.toLowerCase() + "%"));

        return criteriaBuilder.or(predicatesOr.toArray(new Predicate[0]));

    }

    private Predicate postNameOrCodeFilter(String searchWord, Join<PostMember, Post> postJoin, CriteriaBuilder criteriaBuilder) {


        List<Predicate> predicatesOr = new ArrayList<>();

        predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(postJoin.get(Post_.name)),
                "%" + searchWord.toLowerCase() + "%"));

        predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(postJoin.get(Post_.code)),
                "%" + searchWord.toLowerCase() + "%"));

        return criteriaBuilder.or(predicatesOr.toArray(new Predicate[0]));


    }

    private Predicate jobNameOrCodeFilter(String searchWord, Join<Post, Job> jobJoin, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicatesOr = new ArrayList<>();

        predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get(Job_.name)),
                "%" + searchWord.toLowerCase() + "%"));

        predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get(Job_.code)),
                "%" + searchWord.toLowerCase() + "%"));

        return criteriaBuilder.or(predicatesOr.toArray(new Predicate[0]));
    }

}
