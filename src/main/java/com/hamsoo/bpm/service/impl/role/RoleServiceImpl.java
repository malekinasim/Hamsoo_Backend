package com.hamsoo.bpm.service.impl.role;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.PageUtils;
import com.hamsoo.bpm.common.utils.SpecificationUtils;
import com.hamsoo.bpm.model.dto.grantedresourse.GrantedResourceDto;
import com.hamsoo.bpm.model.dto.role.RoleDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.model.entity.role.Role_;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.repository.jpa.role.RoleRepository;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.service.api.grantedresource.GrantedResourceService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;
    private GrantedResourceService grantedResourceService;

    public RoleServiceImpl(RoleRepository roleRepository, GrantedResourceService grantedResourceService) {
        this.roleRepository = roleRepository;
        this.grantedResourceService = grantedResourceService;
    }


    @Override
    public RoleRepository getRepository() {
        return roleRepository;
    }


    @Override
    public Role findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.role.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.role.idIsNotValid"));
    }


    @Override
    public Role getAdminRole() {
        return roleRepository.findByName(Role.OrganRole.ADMIN_ROLE.getCode())
                .orElseThrow(() -> new BusinessException("error.role.idNameNotValid"));

    }

    @Override
    public Role getCollectorRole() {
        return roleRepository.findByName(Role.OrganRole.SUPERVISOR_ROLE.getCode())
                .orElseThrow(() -> new BusinessException("error.role.idNameNotValid"));

    }

    @Override
    public Role getUserRole() {
        return roleRepository.findByName(Role.OrganRole.USER_ROLE.getCode())
                .orElseThrow(() -> new BusinessException("error.role.idNameNotValid"));


    }

    @Override
    public RoleDto mapToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
        roleDto.setStatus(role.getStatus());
        roleDto.setRoleType(role.getRoleType());
        return roleDto;
    }

    @Override
    public RoleDto mapToDtoByResourceInfo(Role role, Long OrganId) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
        roleDto.setStatus(role.getStatus());
        roleDto.setRoleType(role.getRoleType());
        //get role resource based on cur organ sale plan
        List<GrantedResourceDto> resourceDtos = grantedResourceService.findAllByRoleIdAndCurrentOrganSalePlan(role.getId(), OrganId);
        roleDto.setResourceRoleDtoList(resourceDtos);
        return roleDto;
    }

    @Override
    public SearchDto list(RoleDto roleDto, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
        Optional<Specification<Role>> optionalSpecification = createSpecification(roleDto);
        Page<Role> rolePage = findAll(optionalSpecification.get(), pageable);
        List<RoleDto> content = rolePage.map(this::mapToDto).getContent();
        return new SearchDto.Builder().content(content)
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .currentPage(page).pageSize(size).build();
    }

    private Optional<Specification<Role>> createSpecification(RoleDto roleDto) {
        List<Specification<Role>> specificationList = new ArrayList<>();
        Specification<Role> roleActive = (root, query, criteriaBuilder) -> {
            Predicate[] predicates = new Predicate[1];

            predicates[0] = criteriaBuilder.equal(root.get(Role_.STATUS), Status.ACTIVE.name());

            if (roleDto.getRoleType() != null) {
                predicates = ArrayUtils.addAll(predicates, criteriaBuilder.equal(root.get(Role_.ROLE_TYPE),
                        roleDto.getRoleType()));
            }

            if (roleDto.getRoleGroup() != null) {
                if (roleDto.getRoleGroup().equals("USER_ROLE"))
                    predicates = ArrayUtils.addAll(predicates, criteriaBuilder.notEqual(root.get(Role_.NAME), Role.OrganRole.USER_ROLE.getCode()));
            }
            return criteriaBuilder.and(predicates);
        };
        specificationList.add(roleActive);
        return SpecificationUtils.andSpecifications(specificationList);

    }
}