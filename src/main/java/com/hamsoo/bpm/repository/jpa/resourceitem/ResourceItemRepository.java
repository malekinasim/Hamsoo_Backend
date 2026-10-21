package com.hamsoo.bpm.repository.jpa.resourceitem;

import com.hamsoo.bpm.model.entity.resourceitem.ResourceItem;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceItemRepository extends HamsooRepository<ResourceItem,Long> {

    Optional<ResourceItem> findByNameAndStatus(String name, String status);

    List<ResourceItem> findAllByStatus(String status);
}
