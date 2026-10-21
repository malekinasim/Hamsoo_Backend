//package com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.oauth2userinfo;
//
//import java.util.Map;
//
//
//public class GoogleOAuth2UserInfo extends  OAuth2UserInfo{
//    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
//        super(attributes);
//    }
//
//    public GoogleOAuth2UserInfo(Map<String, Object> attributes, Map<String, Object> additionalAttributes) {
//        super(attributes, additionalAttributes);
//
//
//    }
//
//    @Override
//    public String getUserName() {
//        return  this.attributes.get("sub").toString();
//    }
//
//    @Override
//    public String getName() {
//        return (String) this.attributes.get("given_name");
//    }
//
//    @Override
//    public String getFamily() {
//        return (String) this.attributes.get("family_name");
//    }
//
//    @Override
//    public String getEmail() {
//        return (String) this.attributes.get("email");
//    }
//}
