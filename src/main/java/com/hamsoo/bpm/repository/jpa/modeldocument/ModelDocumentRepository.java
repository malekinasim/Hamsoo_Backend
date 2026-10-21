package com.hamsoo.bpm.repository.jpa.modeldocument;

import com.hamsoo.bpm.model.entity.modeldocument.ModelDocument;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface ModelDocumentRepository extends HamsooRepository<ModelDocument, Long> {

    @Query(value = "select md from ModelDocument md inner join md.modelVersion mv inner join mv.model m inner join m.organ o " +
            "where md.id= :id and o.id= :organId and m.status= 'ACTIVE' ")
    Optional<ModelDocument> findByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    List<ModelDocument> findByFileId(@Param("fileId") Long fileId);

    List<ModelDocument> findByModelVersionId(@Param("modelVersionId") Long modelVersionId);

    List<ModelDocument> findAllByModelVersion_IdAndElementId(@Param("modelVersionId") Long modelVersionId, @Param("elementId") String elementId);


    @Query(value = "select COALESCE(sum(file.size),0) from ModelDocument md " +
            "inner join md.modelVersion mv " +
            "inner join mv.model m " +
            "inner join m.organ o " +
            "inner join md.file file " +
            "where  o.id= :organId and m.status = 'ACTIVE'")
    BigInteger calculateOrganModelDocumentCapacity(Long organId);
}
