package com.hamsoo.bpm.service.impl.post;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.post.PostDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.chart.Chart;
import com.hamsoo.bpm.model.entity.chart.Chart_;
import com.hamsoo.bpm.model.entity.job.Job;
import com.hamsoo.bpm.model.entity.job.Job_;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.organ.Organ_;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.model.entity.post.Post_;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.unit.Unit_;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.post.PostRepository;
import com.hamsoo.bpm.service.api.job.JobService;
import com.hamsoo.bpm.service.api.post.PostService;
import com.hamsoo.bpm.service.api.postmember.PostMemberService;
import com.hamsoo.bpm.service.api.unit.UnitService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserService userService;
    private UnitService unitService;
    private JobService jobService;
    private PostMemberService postMemberService;

    public PostServiceImpl(PostRepository postRepository, UserService userService, UnitService unitService,
                           JobService jobService, @Lazy PostMemberService postMemberService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.unitService = unitService;
        this.jobService = jobService;
        this.postMemberService = postMemberService;
    }

    @Override
    public PostRepository getRepository() {
        return postRepository;
    }

    @Override
    public Post findByIdOrElseThrow(Long id) {
        return getRepository().findByIdAndOrganId(id, SecurityUtils.getCurrentOrganIdOrElseThrow()).orElseThrow(
                () -> new BusinessException("error.post.idIsNotValid")
        );
    }

    @Override
    public PostDto mapToDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setName(post.getName());
        postDto.setCode(post.getCode());
        postDto.setDescription(post.getDescription());
        postDto.setUnitId(post.getUnit().getId());
        postDto.setJobId(post.getJob().getId());
        postDto.setJobName(post.getJob().getName());
        postDto.setChartId(post.getUnit().getChart().getId());
        postDto.setId(post.getId());
        List<User> userDtoList= userService.findAllPostMember(post.getId());
        postDto.setUserDtoList(userDtoList.stream().map(u->userService.mapToDto(u)).toList());
        return postDto;
    }

    @Override
    public Post create(PostDto postDto) {
        Assert.notNull(postDto, "postDto is null");
        Post post = new Post();
        post.setName(postDto.getName());
        post.setCode(postDto.getCode());
        post.setDescription(postDto.getDescription());
        post.setStatus(Status.ACTIVE.name());
        post.setValidFrom(LocalDateTime.now());
        post.setJob(jobService.findByIdOrElseThrow(postDto.getJobId()));
        post.setUnit(unitService.findByIdOrElseThrow(postDto.getUnitId()));
        return save(post);
    }

    @Override
    public Post edit(PostDto postDto) {
        Assert.notNull(postDto, "postDto is null");
        Post post = findByIdOrElseThrow(postDto.getId());
        post.setName(postDto.getName());
        post.setDescription(postDto.getDescription());
        return save(post);
    }

    //TODO change on add post to other table like
    //TODO  lane to post add post as owner or add post add modelApprover
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passive(Long id) {
        Post post = findByIdOrElseThrow(id);
        List<PostMember> postMemberList = postMemberService.findByPostIdAndOrganIdAndChartId(post.getId(), post.getUnit().getChart().getId());
        if (!postMemberList.isEmpty())
            throw new BusinessException("error.post.canNotDelete");

        post.setStatus(Status.PASSIVE.name());
        post.setValidTo(LocalDateTime.now());
        save(post);
    }

    @Override
    public SearchDto list(PostDto postDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Post>> optionalSpecification = createSpecification(postDto);
        Page<Post> postPage = findAll(optionalSpecification.get(), pageable);
        List<PostDto> content = postPage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }


    private Optional<Specification<Post>> createSpecification(PostDto postDto) {
        List<Specification<Post>> specificationList = new ArrayList<>();
        Specification<Post> organEq = (root, query, criteriaBuilder) -> {
            Join<Post, Unit> unitJoin = root.join(Post_.UNIT);
            Join<Post, Job> jobJoin = root.join(Post_.JOB);
            Join<Unit, Chart> chartJoin = unitJoin.join(Unit_.CHART);
            Join<Chart, Organ> organJoin = chartJoin.join(Chart_.ORGAN);

            Predicate[] predicates = new Predicate[6];

            predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.STATUS), Status.ACTIVE.name());
            predicates[1] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
            predicates[2] = criteriaBuilder.equal(chartJoin.get(Chart_.STATUS), Status.ACTIVE.name());

            predicates[3] = criteriaBuilder.equal(unitJoin.get(Unit_.STATUS), Status.ACTIVE.name());
            predicates[4] = criteriaBuilder.equal(jobJoin.get(Job_.STATUS), Status.ACTIVE.name());

            predicates[5] = criteriaBuilder.equal(root.get(Post_.STATUS), Status.ACTIVE.name());

            if(postDto.getChartId()!=null){
                predicates = ArrayUtils.addAll(predicates,criteriaBuilder.equal(chartJoin.get(Chart_.ID), postDto.getChartId()));
            }

            if (StringUtils.hasText(postDto.getSearchWord())) {
                List<Predicate> predicatesOr = new ArrayList<>();
                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Post_.NAME)),
                        "%" + postDto.getSearchWord().toLowerCase() + "%"));

                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Post_.CODE)),
                        "%" + postDto.getSearchWord().toLowerCase() + "%"));

                predicatesOr.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(Job_.NAME)),
                        "%" + postDto.getSearchWord().toLowerCase() + "%"));

                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.or(predicatesOr.toArray(new Predicate[0])));
            }


            List<Predicate> predicatesNull = new ArrayList<>();
            if( postDto.getJobId() != null && postDto.getJobId() != 0 )
                predicatesNull.add(criteriaBuilder.equal(jobJoin.get(Job_.id), postDto.getJobId()));

            if(postDto.getUnitId() != null && postDto.getUnitId() != 0 )
                predicatesNull.add(criteriaBuilder.equal(unitJoin.get(Unit_.id), postDto.getUnitId()));

            if(predicatesNull.size()>0)
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.or(predicatesNull.toArray(new Predicate[0])));

            return criteriaBuilder.and(predicates);
        };
        specificationList.add(organEq);
        return SpecificationUtils.andSpecifications(specificationList);
    }

    @Override
    public Optional<Post> findByCodeAndChartId(String code, Long id) {
        return getRepository().findByCodeAndChartIdAndOrganId(code, id, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }

    @Override
    public List<Post> findAllByUnitId(Long unitId) {
        return getRepository().findByUnitIdAndOrganId(unitId, SecurityUtils.getCurrentOrganIdOrElseThrow());

    }

    @Override
    public List<Post> findAllByJobId(Long jobId) {
        return getRepository().findByJobIdAndOrganId(jobId, SecurityUtils.getCurrentOrganIdOrElseThrow());
    }
}
