package com.hamsoo.bpm.service.api.postmember;

import com.hamsoo.bpm.model.dto.postmember.PostMemberDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.postmember.PostMember;
import com.hamsoo.bpm.repository.jpa.postmember.PostMemberRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PostMemberService extends BaseService<PostMember, Long, PostMemberRepository> {
    PostMember findByIdOrElseThrow(Long id);

    PostMemberDto mapToDto(PostMember postMember);

    PostMember create(PostMemberDto postMemberDto);

    void passive(Long id);

    SearchDto list(PostMemberDto postMemberDto, int page, int size, String orderBy, Sort.Direction direction);


    Optional<PostMember> findByPersonIdAndPostIdAndOrganIdAndChartId(Long personId, Long postId, Long chartId);

    List<PostMember> findAllByPersonIdAndOrganId(Long personId);

    List<PostMember> findAllByPersonIdAndAllOrgan(Long personId);

    List<PostMember> findByPostIdAndOrganIdAndChartId(Long id, Long id1);
}
