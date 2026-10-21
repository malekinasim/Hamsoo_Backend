package com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail;

import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;

import java.util.Map;

public class OAuth2UserInfo {
    protected Map<String, Object> attributes;
    protected Map<String, Object> additionalAttributes;
    private OauthProvider oauthProvider;

    public OAuth2UserInfo(Map<String, Object> attributes, OauthProvider oauthProvider) {
        this.attributes = attributes;
        this.oauthProvider = oauthProvider;
    }

    public OAuth2UserInfo(Map<String, Object> attributes, Map<String, Object> additionalAttributes, OauthProvider oauthProvider) {
        this.attributes = attributes;
        this.additionalAttributes = additionalAttributes;
        this.oauthProvider = oauthProvider;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public String getUserName() {
        return (attributes.get(oauthProvider.getUsernameAttr())) != null ? (attributes.get(oauthProvider.getUsernameAttr())).toString() : null;
    }


    public String getName() {
        return attributes.get(oauthProvider.getNameAttr()) != null ? attributes.get(oauthProvider.getNameAttr()).toString() : null;
    }


    public String getFamily() {
        return attributes.get(oauthProvider.getFamilyeAttr()) != null ? attributes.get(oauthProvider.getFamilyeAttr()).toString() : null;
    }


    public String getEmail() {
        return attributes.get(oauthProvider.getMailAttr()) != null ? attributes.get(oauthProvider.getMailAttr()).toString() : null;
    }
}
