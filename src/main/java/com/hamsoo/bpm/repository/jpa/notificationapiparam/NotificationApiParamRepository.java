package com.hamsoo.bpm.repository.jpa.notificationapiparam;

import com.hamsoo.bpm.model.entity.notificationapiparam.NotificationApiParam;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public interface NotificationApiParamRepository extends HamsooRepository<NotificationApiParam, Long> {


}
