package com.ut.security.properties;

/**
 * 社交账号类型 枚举类
 */
public enum SocialAccountTypeEnum {

    SOCIAL_ACCOUNT_TYPE_WECHAT(SocialConstants.SOCIAL_ACCOUNT_TYPE_WECHAT, SocialConstants.WECHAT_TYPE_VALUE),
    SOCIAL_ACCOUNT_TYPE_MINIPROGRAM(SocialConstants.SOCIAL_ACCOUNT_TYPE_MINIPROGRAM, SocialConstants.MINIPROGRAM_TYPE_VALUE)
    ;

    private String socialAccountType;
    private Integer typeValue;

    public String getSocialAccountType() {
        return socialAccountType;
    }

    public Integer getTypeValue() {
        return typeValue;
    }

    SocialAccountTypeEnum(String socialAccountType, Integer typeValue) {
        this.socialAccountType = socialAccountType;
        this.typeValue = typeValue;
    }

    public static Integer getTypeValue(String socialAccountType){
        for(SocialAccountTypeEnum exEnum : SocialAccountTypeEnum.values()){
            if(exEnum.getSocialAccountType() == socialAccountType) {
                return exEnum.getTypeValue();
            }
        }
        return -1;
    }


}
