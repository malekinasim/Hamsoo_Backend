package com.hamsoo.bpm.security.oauth2.userinfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static final String Hamsoo ="hamsoo";
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) throws Exception {

        //TODO add another provider if for example google
        if(registrationId.equalsIgnoreCase(Hamsoo)) {
            return new HamsooUserInfo(attributes);
        } else {
            throw new Exception("متاسفانه ! امکان ورود با  " + registrationId + " وجود ندارد ");
        }
    }
}
