package com.hamsoo.bpm.common.config.security.service.impl.hamsoooauth2userdetail;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.OAuth2UserInfo;
import com.hamsoo.bpm.common.exception.AuthenticationProcessingException;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import org.springframework.util.StringUtils;

import java.util.Map;

public class OAuth2UserInfoFactory {
    // private static String OUATH2_CLASS_KEY = "com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.oauth2userinfo";
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes, Map<String, Object> aditionalattributesa,
                                                   OauthProviderService oauthProviderService) {
        if (StringUtils.hasText(registrationId)) {
            OauthProvider oauthProvider = oauthProviderService.findByRegisterationId(registrationId);
            return new OAuth2UserInfo(attributes, aditionalattributesa, oauthProvider);

//            try {
//                Class<?> clazz  = Class.forName(environment.getProperty(OUATH2_CLASS_KEY +"."+registrationId));
//                Constructor<?> ctor = clazz.getConstructor(Map.class,Map.class);
//                Object object = ctor.newInstance(new Object[] { attributes ,aditionalattributesa});
//                return (OAuth2UserInfo)object;
//            } catch (ClassNotFoundException | NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
            //     return null;
        } else {
            throw new AuthenticationProcessingException("error.oauth2provider.providerNotFound", registrationId);

        }
    }
}
