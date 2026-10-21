package com.hamsoo.bpm.repository.jpa.codecategory;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface CodeCategoryRepository extends HamsooRepository<CodeCategory, Long> {

    Page<CodeCategory> findAll(Specification specification, Pageable pageable);

    CodeCategory findByCategoryAndCode(String category, String code);

    List<CodeCategory> findByCategory(String category, Pageable pageable);
}
