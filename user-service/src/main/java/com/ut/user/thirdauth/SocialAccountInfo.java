package com.ut.user.thirdauth;

import lombok.Data;

/**
 * 自定义微信登录态所对应的数据，主要用于绑定账号时使用
 * @author litingting
 */
@Data
public class SocialAccountInfo {
    /**
     * 该用户社交账号的用户信息
     * */
    String socialEntityInJson;

    /**
     * 该用户的社交账号身份标识
     */
    String uid;

    public SocialAccountInfo() {
    }

    public SocialAccountInfo(String socialEntityInJson, String uid) {
        this.socialEntityInJson = socialEntityInJson;
        this.uid = uid;
    }
}
