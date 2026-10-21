package com.hamsoo.bpm.service.api.model;

import com.hamsoo.bpm.model.dto.model.ModelManagementDto;
import org.springframework.web.multipart.MultipartFile;

public interface ModelManagementService {

    ModelManagementDto create(ModelManagementDto modelManagementDto, MultipartFile xml, MultipartFile image);

}
