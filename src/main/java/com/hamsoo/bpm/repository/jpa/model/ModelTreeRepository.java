package com.hamsoo.bpm.repository.jpa.model;

import com.hamsoo.bpm.model.entity.model.ModelTree;
import com.hamsoo.bpm.repository.jpa.HamsooReadOnlyRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ModelTreeRepository extends HamsooReadOnlyRepository<ModelTree, Long> {


    @Query(value = "select modelTree from  ModelTree  modelTree " +
            "where modelTree.organId=:organId " +
            " and modelTree.state='ACTIVE'" +
            " and modelTree.asisTobeCode=:asisTobeCode " +
            "  and modelTree.parentId is null")
    List<ModelTree> getCurrentOrganParentTreeNode(@Param("organId") Long organId, @Param("asisTobeCode") String asisTobeCode);


    @Query(value = "select modelTree from  ModelTree  modelTree " +
            "where modelTree.organId=:organId " +
            " and modelTree.state='ACTIVE'" +
            " and modelTree.asisTobeCode=:asisTobeCode " +
            "  and modelTree.parentId=:parentId")
    List<ModelTree> getCurrentOrganChildTreeNode(@Param("organId") Long organId, @Param("asisTobeCode") String asisTobeCode, @Param("parentId") Long parentId);

    @Query(value = "select modelTree from  ModelTree  modelTree " +
            "where modelTree.organId=:organId " +
            " and modelTree.state='ACTIVE'" +
            " and modelTree.asisTobeCode=:asisTobeCode ")
    List<ModelTree> getCurrentOrganModelTree(@Param("organId") Long organId, @Param("asisTobeCode") String asisTobeCode);

}
