//package com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.oauth2userinfo;
//
//import java.util.Map;
//
//public class GithubOAuth2UserInfo extends  OAuth2UserInfo {
//    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
//        super(attributes);
//    }
//
//    public GithubOAuth2UserInfo(Map<String, Object> attributes,Map<String, Object> additinalAtrributes) {
//        super(attributes,additinalAtrributes);
//    }
//
//    @Override
//    public String getUserName() {
//        return ((Integer) attributes.get("id")).toString();
//    }
//
//    @Override
//    public String getName() {
//        return (String) attributes.get("name");
//    }
//
//    @Override
//    public String getFamily() {
//        return (String) attributes.get("name");
//    }
//
//    @Override
//    public String getEmail() {
//        return (String) attributes.get("email");
//    }
//}
