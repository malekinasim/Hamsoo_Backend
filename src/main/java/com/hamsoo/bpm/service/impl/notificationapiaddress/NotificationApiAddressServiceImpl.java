package com.hamsoo.bpm.service.impl.notificationapiaddress;


import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.entity.notificationapiaddress.NotificationApiAddress;
import com.hamsoo.bpm.service.api.notificationapiaddress.NotificationApiAddressService;
import com.hamsoo.bpm.repository.jpa.notificationapiaddress.NotificationApiAddressRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationApiAddressServiceImpl implements NotificationApiAddressService {

    private final NotificationApiAddressRepository notificationApiAddressRepository;

    public NotificationApiAddressServiceImpl(NotificationApiAddressRepository notificationApiAddressRepository) {
        this.notificationApiAddressRepository = notificationApiAddressRepository;
    }

    @Override
    public NotificationApiAddressRepository getRepository() {
        return notificationApiAddressRepository;
    }

    public NotificationApiAddress findByCodeOrElseThrow(String code) {
        if (code == null) {
            throw new BusinessException("error.notificationApiAddress.codeIsNotValid");
        }
        return getRepository().findByCode(code)
                .orElseThrow(() -> new BusinessException("error.notificationApiAddress.codeIsNotValid"));
    }
}
