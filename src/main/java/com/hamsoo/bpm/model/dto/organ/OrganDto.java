package com.hamsoo.bpm.model.dto.organ;

import com.hamsoo.bpm.model.dto.organplan.OrganPlanDto;
import com.hamsoo.bpm.model.dto.rolemember.RoleMemberDto;
import com.hamsoo.bpm.model.dto.oauthprovider.OauthProviderDto;
import com.hamsoo.bpm.model.entity.organ.Organ;

import java.math.BigInteger;
import java.util.List;


public class OrganDto {
    private Long id;
    //search return
    private String name;
    private String description;
    private String status;
    private Boolean aDefault;
    //search return
    private Organ.OrganType organType;
    private Long ownerUserId;
    private String ownerName;
    private List<RoleMemberDto> roleMemberDtos;
    private String organCode;
    private OrganPlanDto lastOrganPlan;
    private OauthProviderDto oauthProviderDto;
    private Long salePlanId;
    private Integer maxUserCount;
    private Integer maxModelCount;
    private BigInteger maxCapacity;
    private BigInteger amountPaid;

    private Long logoId;


    public OrganDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Boolean getDefault() {
        return aDefault;
    }

    public void setDefault(Boolean aDefault) {
        this.aDefault = aDefault;
    }


    public Organ.OrganType getOrganType() {
        return organType;
    }

    public void setOrganType(Organ.OrganType organType) {
        this.organType = organType;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Boolean getaDefault() {
        return aDefault;
    }

    public void setaDefault(Boolean aDefault) {
        this.aDefault = aDefault;
    }

    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public String getOrganCode() {
        return organCode;
    }

    public void setOrganCode(String organCode) {
        this.organCode = organCode;
    }


    public List<RoleMemberDto> getRoleMemberDtos() {
        return roleMemberDtos;
    }

    public void setRoleMemberDtos(List<RoleMemberDto> roleMemberDtos) {
        this.roleMemberDtos = roleMemberDtos;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OauthProviderDto getOauthProviderDto() {
        return oauthProviderDto;
    }

    public void setOauthProviderDto(OauthProviderDto oauthProviderDto) {
        this.oauthProviderDto = oauthProviderDto;
    }

    public OrganPlanDto getLastOrganPlan() {
        return lastOrganPlan;
    }

    public void setLastOrganPlan(OrganPlanDto lastOrganPlan) {
        this.lastOrganPlan = lastOrganPlan;
    }

    public Long getSalePlanId() {
        return salePlanId;
    }

    public void setSalePlanId(Long salePlanId) {
        this.salePlanId = salePlanId;
    }

    public Integer getMaxUserCount() {
        return maxUserCount;
    }

    public void setMaxUserCount(Integer maxUserCount) {
        this.maxUserCount = maxUserCount;
    }

    public Integer getMaxModelCount() {
        return maxModelCount;
    }

    public void setMaxModelCount(Integer maxModelCount) {
        this.maxModelCount = maxModelCount;
    }

    public BigInteger getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(BigInteger maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public BigInteger getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigInteger amountPaid) {
        this.amountPaid = amountPaid;
    }
}
