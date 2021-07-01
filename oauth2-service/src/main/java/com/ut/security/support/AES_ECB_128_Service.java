package com.ut.security.support;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Component
public class AES_ECB_128_Service {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
//    private static final String ALGORITHM = "AES/ECB/NoPadding";
    private static final String CHARSET = "UTF-8";
//    private static final String CHARSET = "GBK";
    private static final String AES_KEY = "UT34567890123401";//此处使用AES-128-ECB加密模式，key需要为16位。
    private static final String AES_VERIFY_CONSTANT = "UT_OAUTH";//加密常量内容

    /**
     * 生成加密会话token
     */
    public String getSecurityToken(){
        String encryStr = AES_VERIFY_CONSTANT + ":" + System.currentTimeMillis();
        String token = null;
        try {
            token = Encrypt(encryStr, AES_KEY);
        } catch (Exception e) {
            throw new AuthenticationServiceException("生成加密token失败！");
        }
        return token;
    }

    /**
     * 校验会话token
     */
    public boolean checkSecurityToken(String token)throws Exception{
        token = Decrypt(token, AES_KEY);
        String[] content = token.split(":");
        if(content.length < 2)
            throw new AccessDeniedException("您无权访问！");
        if(!AES_VERIFY_CONSTANT.equals(content[0]))
            throw new AccessDeniedException("您无权访问！");
        return true;
    }

    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes(CHARSET);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(CHARSET));

        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes(CHARSET);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,CHARSET);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "UT34567890123401";
        // 需要加密的字串
        String cSrc = "优特智厨科技有限公司ABC-def123";//12345678901234567

        System.out.println("使用算法："+ AES_ECB_128_Service.ALGORITHM);
        System.out.println("使用字符集："+ AES_ECB_128_Service.CHARSET);
        System.out.println("待加密字符串长度:"+ cSrc.length());

        // 加密
        String enString = AES_ECB_128_Service.Encrypt(cSrc, cKey);
        System.out.println("AESECB128加密后的字串是：" + enString);

        // 解密
        String DeString = AES_ECB_128_Service.Decrypt(enString, cKey);
        System.out.println("AESECB128解密后的字串是：" + DeString);

        System.out.println("=======================token机制=======================");
        AES_ECB_128_Service aes = new AES_ECB_128_Service();
        String token = aes.getSecurityToken();
        System.out.println("token = " + token);
        System.out.println(aes.checkSecurityToken(token));


    }
}