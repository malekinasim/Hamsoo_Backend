package com.hamsoo.bpm.service.api.bpmnreport;

import com.hamsoo.bpm.model.dto.BpmnReport.BpmnReportDto;

import java.util.List;

public interface BpmnReportService {//extends BaseService<ApproverLog, Long, ApproverLogRepository>  {


    public List<BpmnReportDto> listModelDecisions();

//    SearchDto listPeopleActivities(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction);
//
//    SearchDto listModelDecisions(BpmnReportDto bpmnReportDto, int page, int size, String orderBy, Sort.Direction direction);

//    List<BpmnReportDto> listUserActivities();

//    SearchDto listModelDecisions(ModelDto modelDto, int page, int size, String orderBy, Sort.Direction direction);
}
