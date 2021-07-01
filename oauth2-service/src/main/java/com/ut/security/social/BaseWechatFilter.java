package com.ut.security.social;

import com.ut.security.browser.wechat.WechatLoginAuthenticationToken;
import com.ut.security.feign.FeignUserService;
import com.ut.security.properties.SecurityConstants;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.rbac.thirdaccount.wechat.ThirdAccountConstant;
import com.ut.security.social.account.SocialAccountServiceProvider;
import com.ut.security.social.account.SocialAccountServices;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class BaseWechatFilter extends AbstractAuthenticationProcessingFilter {

    public BaseWechatFilter(String fileterUrl) {
        super(new AntPathRequestMatcher(fileterUrl, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String loginKey = request.getParameter(SecurityConstants.SOCIAL_ACCOUNT_KEY);
        log.info("微信 socialAccountKey=" + loginKey);

        SocialLoginService socialService = SpringUtils.getBean(SocialLoginService.class);
        String socialAccountInJson;
        try {
            socialAccountInJson = socialService.getLoginKey(loginKey);
        } catch (Exception e) {
            throw new AuthenticationServiceException("无法找到自定义登录key，请查看是否过期或有效，key=" + loginKey);
        }

        SocialAccountServiceProvider socialAccountServiceProvider = SpringUtils.getBean(SocialAccountServiceProvider.class);
        SocialAccountServices thirdAccountService = socialAccountServiceProvider.getAccountService(ThirdAccountConstant.WECHAT);

        // 检查微信用户是不是已经存在本系统
        String oauthUser = thirdAccountService.getOauthUserName(socialAccountInJson);
        if (oauthUser == null) {
            throw new AuthenticationServiceException("该微信用户未在系统中注册，请先注册");
        }

        //获取用户主键
        FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
        AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
        MyUserEntity userByUsername = feignUserService.getUserByUsername(oauthUser, aes.getSecurityToken());
        if (null == userByUsername) {
            throw new AuthenticationServiceException("获取用户信息异常！");
        }

        WechatLoginAuthenticationToken authRequest = new WechatLoginAuthenticationToken(userByUsername.getUsername());
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private void setDetails(HttpServletRequest request, WechatLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
