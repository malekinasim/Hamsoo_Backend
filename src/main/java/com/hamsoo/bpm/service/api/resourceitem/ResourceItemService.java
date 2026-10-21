package com.hamsoo.bpm.service.api.resourceitem;

import com.hamsoo.bpm.common.exception.ErrorDetails;
import com.hamsoo.bpm.model.dto.resourceitem.ResourceItemDto;
import com.hamsoo.bpm.model.entity.resourceitem.ResourceItem;
import com.hamsoo.bpm.repository.jpa.resourceitem.ResourceItemRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

public interface ResourceItemService extends BaseService<ResourceItem, Long, ResourceItemRepository> {
    public static final String
            ASIS_TOBE_VERSION_MNG = ResourceItem.ResouceItemType.ASIS_TOBE_VERSION_MNG.name(),
            MODEL_EDIT = ResourceItem.ResouceItemType.MODEL_EDIT.name(),
            MODEL_COMMENT_MNG = ResourceItem.ResouceItemType.MODEL_COMMENT_MNG.name(),
            EM_MODEL_LANE_CATEGORY_MNG = ResourceItem.ResouceItemType.EM_MODEL_LANE_CATEGORY_MNG.name(),
            MODEL_TREE_MNG = ResourceItem.ResouceItemType.MODEL_TREE_MNG.name(),
            SALE_PLAN_MNG = ResourceItem.ResouceItemType.SALE_PLAN_MNG.name(),
            MODEL_REFERENCE_MNG = ResourceItem.ResouceItemType.MODEL_REFERENCE_MNG.name(),
            MODEL_REFERENCE_APPROVER = ResourceItem.ResouceItemType.MODEL_REFERENCE_APPROVER.name(),
            MODEL_REFERENCE_DEFER = ResourceItem.ResouceItemType.MODEL_REFERENCE_DEFER.name(),
            MODEL_REFERENCE_ACKNOWLEDGE = ResourceItem.ResouceItemType.MODEL_REFERENCE_ACKNOWLEDGE.name(),
           // MODEL_NOTIFICATION_LIST = ResourceItem.ResouceItemType.MODEL_NOTIFICATION_LIST.name(),
            MODEL_TASK_ASSIGN_MNG = ResourceItem.ResouceItemType.MODEL_TASK_ASSIGN_MNG.name(),
            REFERENCE_MODELS_LIST = ResourceItem.ResouceItemType.REFERENCE_MODELS_LIST.name(),
            MODEL_IMAGE_FILE = ResourceItem.ResouceItemType.MODEL_IMAGE_FILE.name(),
            MODEL_XML_FILE = ResourceItem.ResouceItemType.MODEL_XML_FILE.name(),
            MODEL_RENAME = ResourceItem.ResouceItemType.MODEL_RENAME.name(),
            MODEL_REMOVE = ResourceItem.ResouceItemType.MODEL_REMOVE.name(),
            MODEL_SIPOC_REPORT = ResourceItem.ResouceItemType.MODEL_SIPOC_REPORT.name(),
            MODEL_INFO = ResourceItem.ResouceItemType.MODEL_INFO.name(),
            MODEL_CARD_INFO = ResourceItem.ResouceItemType.MODEL_CARD_INFO.name(),
            PROCESS_GENERAL_MNG = ResourceItem.ResouceItemType.PROCESS_GENERAL_MNG.name(),
            MODEL_RESOURCE_EDIT = ResourceItem.ResouceItemType.MODEL_RESOURCE_EDIT.name(),
            MODEL_CREATE = ResourceItem.ResouceItemType.MODEL_CREATE.name(),
            PROCESS_TREE = ResourceItem.ResouceItemType.PROCESS_TREE.name(),
            MODEL_DOCUMENT_EDIT = ResourceItem.ResouceItemType.MODEL_DOCUMENT_EDIT.name(),
            MODEL_KPI_EDIT = ResourceItem.ResouceItemType.MODEL_KPI_EDIT.name(),
            MODEL_INPUT_OUTPUT_EDIT = ResourceItem.ResouceItemType.MODEL_INPUT_OUTPUT_EDIT.name(),
            MODEL_CONS_SUP_EDIT = ResourceItem.ResouceItemType.MODEL_CONS_SUP_EDIT.name(),
            WORKSPACE_MNG = ResourceItem.ResouceItemType.WORKSPACE_MNG.name(),
            EXTERNAL_PROCESS_MNG = ResourceItem.ResouceItemType.EXTERNAL_PROCESS_MNG.name(),
            INPUT_OUTPUT_MNG = ResourceItem.ResouceItemType.INPUT_OUTPUT_MNG.name(),
            IO_TYPE_MNG = ResourceItem.ResouceItemType.IO_TYPE_MNG.name(),
            KPI_MNG = ResourceItem.ResouceItemType.KPI_MNG.name(),
            KPI_TREND_TYPE_LIST = ResourceItem.ResouceItemType.KPI_TREND_TYPE_LIST.name(),
            KPI_MONITOR_TYPE_LIST = ResourceItem.ResouceItemType.KPI_MONITOR_TYPE_LIST.name(),
            KPI_DURATION_TYPE_LIST = ResourceItem.ResouceItemType.KPI_DURATION_TYPE_LIST.name(),
            ORGAN_MEMBER_MNG = ResourceItem.ResouceItemType.ORGAN_MEMBER_MNG.name(),
            PROCESS_CATEGORY_MNG = ResourceItem.ResouceItemType.PROCESS_CATEGORY_MNG.name(),
            RESOURCE_MNG = ResourceItem.ResouceItemType.RESOURCE_MNG.name(),
            RESOURCE_TYPE_MNG = ResourceItem.ResouceItemType.RESOURCE_TYPE_MNG.name(),
            UNIT_MNG = ResourceItem.ResouceItemType.UNIT_MNG.name(),
            MODEL_MEMBERS_MANAGE = ResourceItem.ResouceItemType.MODEL_MEMBERS_MANAGE.name(),
            MODEL_MEMBERS_LIST = ResourceItem.ResouceItemType.MODEL_MEMBERS_LIST.name(),
            ORGANIZATIONAL_CHART_MNG = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_MNG.name(),
            ORGANIZATIONAL_CHART_EDIT = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_EDIT.name(),
            ORGANIZATIONAL_CHART_TREE = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_TREE.name(),
            ORGANIZATIONAL_CHART_POST_MNG = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_POST_MNG.name(),
            ORGANIZATIONAL_CHART_POST_MEMBER_MNG = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_POST_MEMBER_MNG.name(),
            ORGANIZATIONAL_CHART_UNIT_MNG = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_UNIT_MNG.name(),
            ORGANIZATIONAL_CHART_PERSON_MNG = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_PERSON_MNG.name(),
            ORGANIZATIONAL_CHART_JOB_MNG = ResourceItem.ResouceItemType.ORGANIZATIONAL_CHART_JOB_MNG.name(),
            MODEL_DECISION_REPORT = ResourceItem.ResouceItemType.MODEL_DECISION_REPORT.name(),
            MILESTONES_MNG = ResourceItem.ResouceItemType.MILESTONES_MNG.name(),
            FINANCIAL_PANEL = ResourceItem.ResouceItemType.FINANCIAL_PANEL.name(),
           MODEL_Risk_ADD = ResourceItem.ResouceItemType.MODEL_Risk_ADD.name(),
           MODEL_Risk_DELETE = ResourceItem.ResouceItemType.MODEL_Risk_DELETE.name(),
           MODEL_Risk_EDIT = ResourceItem.ResouceItemType.MODEL_Risk_EDIT.name(),
           MODEL_VALIDATION_REPORT = ResourceItem.ResouceItemType.MODEL_VALIDATION_REPORT.name(),
           MODEL_Complication_ADD = ResourceItem.ResouceItemType.MODEL_Complication_ADD.name(),
           MODEL_Complication_DELETE = ResourceItem.ResouceItemType.MODEL_Complication_DELETE.name(),
           MODEL_Complication_EDIT = ResourceItem.ResouceItemType.MODEL_Complication_EDIT.name();

    ResourceItemDto mapToDto(ResourceItem roleResourceItem);

    Optional<ResourceItem> finsByName(String name);

    ErrorDetails getResourceItemsAccessDeniedMsg(List<String> resourceNames) throws UnknownHostException;

    List<ResourceItemDto> list() throws UnknownHostException;

    ResourceItemDto getResourceItemByNamAndErrorDetails(String name) throws UnknownHostException;
}
