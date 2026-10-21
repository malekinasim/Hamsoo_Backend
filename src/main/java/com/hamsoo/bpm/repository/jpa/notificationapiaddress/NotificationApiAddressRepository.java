package com.hamsoo.bpm.repository.jpa.notificationapiaddress;

import com.hamsoo.bpm.model.entity.notificationapiaddress.NotificationApiAddress;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(rollbackFor = Exception.class)
public interface NotificationApiAddressRepository extends HamsooRepository<NotificationApiAddress, Long> {


    Optional<NotificationApiAddress> findByCode(String code);

}
