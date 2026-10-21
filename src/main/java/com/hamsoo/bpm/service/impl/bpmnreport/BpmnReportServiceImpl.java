package com.hamsoo.bpm.service.impl.bpmnreport;

import com.hamsoo.bpm.common.utils.BpmnHelperService;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.BpmnReport.BpmnReportDto;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.entity.model.Model;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.service.api.bpmnreport.BpmnReportService;
import com.hamsoo.bpm.service.api.model.ModelService;
import com.hamsoo.bpm.service.api.modelversion.ModelVersionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BpmnReportServiceImpl implements BpmnReportService {

  private final ModelVersionService modelVersionService;
  private final ModelService modelService;

  public BpmnReportServiceImpl(ModelVersionService modelVersionService, ModelService modelService) {
    this.modelVersionService = modelVersionService;
    this.modelService = modelService;
  }


//  @Transactional(readOnly = true)
//  public SearchDto listPeopleActivities(BpmnReportDto bpmnReportDto, int page, int size, String orderBy, Sort.Direction direction) {
//    Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
//    Optional<Specification<Model>> optionalSpecification = createSpecification(bpmnReportDto, true);
//    Page<Model> modelPage = findAll(optionalSpecification.get(), pageable);
//    List<ModelDto> content = modelPage.map(this::mapToDtoWithLastVersionData).getContent();
//    return new SearchDto.Builder().content(content)
//            .totalElements(modelPage.getTotalElements())
//            .totalPages(modelPage.getTotalPages())
//            .currentPage(page)
//            .pageSize(size).build();
//  }
//
//
//
//  @Transactional(readOnly = true)
//  public SearchDto listModelDecisions(BpmnReportDto bpmnReportDto, int page, int size, String orderBy, Sort.Direction direction) {
//    Pageable pageable = PageUtils.getInstance(page, size, orderBy, direction);
//    Optional<Specification<Model>> optionalSpecification = createSpecification(bpmnReportDto, true);
//    Page<Model> modelPage = findAll(optionalSpecification.get(), pageable);
//    List<ModelDto> content = modelPage.map(this::mapToDtoWithLastVersionData).getContent();
//    return new SearchDto.Builder().content(content)
//            .totalElements(modelPage.getTotalElements())
//            .totalPages(modelPage.getTotalPages())
//            .currentPage(page)
//            .pageSize(size).build();
//  }

//  public Optional<Specification<Model>> createSpecification(BpmnReportDto bpmnReportDto, boolean filterByMininVersion) {
//    List<Specification<Model>> specificationList = new ArrayList<>();
//    //    if(filterByModelMember)
//    specificationList.add(modelExist(modelDto));
//
//    specificationList.add(modelVersionPredicates(modelDto, filterByMininVersion));
//
//    Specification<Model> organEq = (root, query, criteriaBuilder) -> {
//      Join<Model, Organ> organJoin = root.join(Model_.ORGAN);
//      Predicate[] predicates = new Predicate[2];
//      predicates[0] = criteriaBuilder.equal(organJoin.get(Organ_.ID), SecurityUtils.getCurrentOrganIdOrElseThrow());
//      predicates[1] = criteriaBuilder.equal(root.get(Model_.STATUS), Status.ACTIVE.name());
//      return criteriaBuilder.and(predicates);
//    };
//    specificationList.add(organEq);
//
//    if (modelDto.getName() != null) {
//      Specification<Model> nameLike = (root, query, criteriaBuilder) ->
//              criteriaBuilder.like(root.get(Model_.NAME), "%" + modelDto.getName() + "%");
//      specificationList.add(nameLike);
//    }
//
//    if (!CollectionUtils.isEmpty(modelDto.getModelTypeCodeList())) {
//      Specification<Model> modelTypeCodeIn = (root, query, criteriaBuilder) -> {
//        Join<Model, CodeCategory> modelTypeJoin = root.join(Model_.MODEL_TYPE);
//        return modelTypeJoin.get(CodeCategory_.CODE).in(modelDto.getModelTypeCodeList());
//      };
//      specificationList.add(modelTypeCodeIn);
//    }
//
//    if (modelDto.getFavorite() != null && modelDto.getFavorite()) {
//      specificationList.add(userFavoriteExist());
//    }
//    return SpecificationUtils.andSpecifications(specificationList);
//  }



  public List<BpmnReportDto> listModelDecisions()
  {

    List<BpmnReportDto> BpmnReportDtoFinalList = null ;

    //  List<Model> models = modelService.getRepository().fi.findByModelTypeCodeAndOrganId("BP", SecurityUtils.getCurrentOrganIdOrElseThrow());
    List<Model> models = modelService.findAll().stream().filter(m->m.getStatus().equals("ACTIVE") && m.getOrgan().getId().equals(SecurityUtils.getCurrentOrganIdOrElseThrow())).toList();
         //  .stream()
            //.filter(m->m.getModelVersions()..getAsisTobe().getCategory().equals("AS_IS")
    //   && m.getStatus().equals("ACTIVE") && m.getLastVersion();

      for (Model model : models) {
        Optional<ModelVersion> lastModelVersion = model.getModelVersions().stream().filter(m->//m.getAsisTobe().getCategory().equals("AS_IS")
               //&&
               // m.getStatus().getCategory().equals("ACTIVE")
                m.getLastVersion()
      ).findAny();
      if(!lastModelVersion.isEmpty()) {
        long modelVersionId = lastModelVersion.get().getId();
        FilesDto xmlFile = modelVersionService.getXml(modelVersionId);//modelTaskAssigneeDto.getModelVersionId());
        BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());
        BpmnReportDtoFinalList.addAll(bpmnHelperService.getModelDecisions());
      }
    }
    return BpmnReportDtoFinalList;//.stream().toList().stream().skip().limit().sorted();

  }
//  public List<BpmnReportDto> listUserActivities()
//  {
//
//    List<BpmnReportDto> BpmnReportDtoFinalList = null ;
//
//    //  List<Model> models = modelService.getRepository().fi.findByModelTypeCodeAndOrganId("BP", SecurityUtils.getCurrentOrganIdOrElseThrow());
//    List<Model> models = modelService.findAll().stream().filter(m->m.getStatus().equals("ACTIVE") && m.getOrgan().getId().equals(SecurityUtils.getCurrentOrganIdOrElseThrow())).toList();
//    //  .stream()
//    //.filter(m->m.getModelVersions()..getAsisTobe().getCategory().equals("AS_IS")
//    //   && m.getStatus().equals("ACTIVE") && m.getLastVersion();
//
//    for (Model model : models) {
//      Optional<ModelVersion> lastModelVersion = model.getModelVersions().stream().filter(m->//m.getAsisTobe().getCategory().equals("AS_IS")
//              //&&
//              // m.getStatus().getCategory().equals("ACTIVE")
//              m.getLastVersion()
//      ).findAny();
//      if(!lastModelVersion.isEmpty()) {
//        long modelVersionId = lastModelVersion.get().getId();
//        FilesDto xmlFile = modelVersionService.getXml(modelVersionId);//modelTaskAssigneeDto.getModelVersionId());
//        BpmnHelperService bpmnHelperService = new BpmnHelperService(xmlFile.getXmlContent());
//        BpmnReportDtoFinalList.addAll(bpmnHelperService.getModelDecisions());
//      }
//    }
//    return BpmnReportDtoFinalList;
//
//  }
}
