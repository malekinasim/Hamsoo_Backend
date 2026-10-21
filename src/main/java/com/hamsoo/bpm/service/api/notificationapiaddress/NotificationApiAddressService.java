package com.hamsoo.bpm.service.api.notificationapiaddress;

import com.hamsoo.bpm.model.entity.notificationapiaddress.NotificationApiAddress;
import com.hamsoo.bpm.repository.jpa.notificationapiaddress.NotificationApiAddressRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

public interface NotificationApiAddressService extends BaseService<NotificationApiAddress, Long, NotificationApiAddressRepository> {

    NotificationApiAddress findByCodeOrElseThrow(String code);

}
