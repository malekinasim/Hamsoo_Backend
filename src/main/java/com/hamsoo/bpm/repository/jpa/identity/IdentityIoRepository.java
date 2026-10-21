package com.hamsoo.bpm.repository.jpa.identity;

import com.hamsoo.bpm.model.entity.identity.IdentityIo;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface IdentityIoRepository extends HamsooRepository<IdentityIo, Long> {

    @Query("select ii from IdentityIo ii inner join ii.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where ii.id = :id and o.id = :organId and ii.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    Optional<IdentityIo> findActiveByIdAndOrganId(@Param("id") Long id, @Param("organId") Long organId);

    @Query("select ii from IdentityIo ii inner join ii.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join ii.type t inner join ii.ioCatalog ic " +
            "where o.id = :organId and t.code = :typeCode and ic.id = :ioCatalogId " +
            "and mv.id = :modelVersionId  and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    Optional<IdentityIo> findEqualActiveIdentityIo(@Param("organId") Long organId, @Param("typeCode") String typeCode,
                                                   @Param("ioCatalogId") Long ioCatalogId, @Param("modelVersionId") Long modelVersionId);

    @Query("select ii from IdentityIo ii inner join ii.modelVersion mv inner join mv.model m " +
            "inner join m.organ o " +
            "where o.id = :organId and mv.id = :modelVersionId and ii.status = 'ACTIVE' and m.status = 'ACTIVE'  ")
    List<IdentityIo> findActiveByModelVersionId(@Param("organId") Long organId, @Param("modelVersionId") Long modelVersionId);

    @Query("select count(ii.id) from IdentityIo ii inner join ii.ioCatalog ic inner join ii.modelVersion mv inner join mv.model m " +
            "where ic.id = :ioCatalogId and ii.status = 'ACTIVE' and m.status = 'ACTIVE' and ic.status = 'ACTIVE'  ")
    Long countByIoCatalogId(@Param("ioCatalogId") Long ioCatalogId);


    @Query("select ii from IdentityIo ii inner join ii.modelVersion mv inner join mv.model m " +
            "inner join m.organ o inner join ii.type t  " +
            "where o.id = :organId and t.code = :typeCode  " +
            "and mv.id = :modelVersionId  and ii.status = 'ACTIVE' and m.status = 'ACTIVE' ")
    List<IdentityIo> findAllInternalActiveByModelVersionId(@Param("organId") Long organId, @Param("typeCode") String typeCode, @Param("modelVersionId") Long modelVersionId);


}
