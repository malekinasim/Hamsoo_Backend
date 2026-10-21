package com.hamsoo.bpm.service.api.feature;

import com.hamsoo.bpm.model.dto.feature.FeatureDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.entity.feature.Feature;
import com.hamsoo.bpm.repository.jpa.feature.FeatureRepository;
import com.hamsoo.bpm.service.api.base.BaseService;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public interface FeatureService extends BaseService<Feature,Long, FeatureRepository> {
    Feature findByIdOrElseThrow(Long id);

    FeatureDto mapToDto(Feature feature);

    Feature create(FeatureDto featureDto);

    Feature edit(FeatureDto featureDto);

    void passive(Long id);

    SearchDto list(FeatureDto featureDto, int page, int size, String orderBy, Sort.Direction direction);

    Optional<Feature> findByFeatureCalcType(String calcType);
}
