package com.hamsoo.bpm.model.dto.oauthprovider;

import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;


public class OauthProviderDto {
    private static String PRE_AUTHORIZE_URI = "/oauth2/authorize/";
    private static String REDIRECT_URL_PARAM = "?redirect_uri=";
    private Long organId;
    private String url;
    private String name;
    private byte[] logo;
    private String logoMimType;
    private String registerationId;

    public OauthProviderDto() {
    }

    public OauthProviderDto(OauthProvider oauthProvider) {
        this(null, null, null, null, oauthProvider.getRegisterationId(),
                oauthProvider.getOrgan()!=null?oauthProvider.getOrgan().getId() :null);


    }


    public OauthProviderDto(OauthProvider oauthProvider, String redirectUri, String serverAddress) {

        this(oauthProvider.getType().equals(
                OauthProvider.providerType.OIDC.name()) ?
                        serverAddress + PRE_AUTHORIZE_URI + oauthProvider.getRegisterationId() + REDIRECT_URL_PARAM + redirectUri :
                        null,
                oauthProvider.getOrgan().getDescription(),
                oauthProvider.getOrgan().getLogo() != null ?
                        oauthProvider.getOrgan().getLogo().getByteContent() : null,
                oauthProvider.getOrgan().getLogo() != null ?
                        oauthProvider.getOrgan().getLogo().getMimeType() : null,
                oauthProvider.getRegisterationId(),
                oauthProvider.getOrgan()!=null?oauthProvider.getOrgan().getId() :null);


    }

    public OauthProviderDto(String url, String name, byte[] logo, String logoMimType, String registerationId,Long organId) {
        this.url = url;
        this.name = name;
        this.logo = logo;
        this.logoMimType = logoMimType;
        this.registerationId = registerationId;
        this.organId=organId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoMimType() {
        return logoMimType;
    }

    public void setLogoMimType(String logoMimType) {
        this.logoMimType = logoMimType;
    }


    public static String getPreAuthorizeUri() {
        return PRE_AUTHORIZE_URI;
    }

    public static String getRedirectUrlParam() {
        return REDIRECT_URL_PARAM;
    }

    public String getRegisterationId() {
        return registerationId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public void setRegisterationId(String registerationId) {
        this.registerationId = registerationId;
    }
}
