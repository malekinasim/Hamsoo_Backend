package com.hamsoo.bpm.service.api.post;

import com.hamsoo.bpm.model.dto.post.PostDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.post.Post;
import com.hamsoo.bpm.repository.jpa.post.PostRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PostService extends BaseService<Post, Long, PostRepository> {
    Post findByIdOrElseThrow(Long id);

    PostDto mapToDto(Post post);

    Post create(PostDto postDto);

    Post edit(PostDto postDto);

    void passive(Long id);

    SearchDto list(PostDto postDto, int page, int size, String orderBy, Sort.Direction direction);

    Optional<Post> findByCodeAndChartId(String code, Long id);

    List<Post> findAllByUnitId(Long id);

    List<Post> findAllByJobId(Long id);
}
