package com.hamsoo.bpm.repository.jpa.files;

import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface FilesRepository extends HamsooRepository<Files, Long> {


}