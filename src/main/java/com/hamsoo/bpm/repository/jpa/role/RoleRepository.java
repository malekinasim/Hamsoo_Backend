package com.hamsoo.bpm.repository.jpa.role;

import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface RoleRepository extends HamsooRepository<Role, Long> {

    Page<Role> findAll(Specification specification, Pageable pageable);

    Optional<Role> findByName(String name);
}