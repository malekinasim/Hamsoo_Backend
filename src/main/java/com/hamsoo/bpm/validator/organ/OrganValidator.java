package com.hamsoo.bpm.validator.organ;

import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.rolemember.RoleMemberDto;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.role.Role;
import com.hamsoo.bpm.service.api.organ.OrganService;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;

@Component
public class OrganValidator extends HamsooValidator {

    private final OrganService organService;
    private final RoleService roleService;

    public OrganValidator(OrganService organService, RoleService roleService) {
        this.organService = organService;
        this.roleService = roleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(OrganDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        OrganDto organDto = (OrganDto) target;



            if (organDto.getName() == null) {
                errors.rejectValue("name", "", "error.organ.nameIsNotValid");
            }
            if (organDto.getId() == null) {
                try {
                    organService.findByNameOrElseThrow(organDto.getName());
                    errors.rejectValue("name", "", "error.organ.organNameIsDuplicate");
                } catch (Exception e) {

                }
            }

        if (organDto.getRoleMemberDtos() != null && organDto.getRoleMemberDtos().size() == 0) {
                errors.rejectValue("roleMemberDtos", "", "error.organ.organMustHaveAdmin");
            } else {
                Role role = roleService.getAdminRole();
                List<RoleMemberDto> roleMembers = organDto.getRoleMemberDtos().stream().filter(item -> item.getRoleId().equals(role.getId())).toList();
                if (roleMembers.size() == 0) {
                    errors.rejectValue("roleMemberDtos", "", "error.organ.organMustHaveAdmin");
                } else if (roleMembers.size() > 1 && organDto.getOrganType().equals(Organ.OrganType.PRIVATE)) {
                    errors.rejectValue("roleMemberDtos", "", "error.organ.organPublicOrganMustHaveMaxOneAdmin");
                }
            }
   }


}
