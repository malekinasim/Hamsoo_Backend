package com.hamsoo.bpm.security.oauth2.userinfo;

import java.util.Map;

public class HamsooUserInfo extends OAuth2UserInfo {
    public HamsooUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

}
