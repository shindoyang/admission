package com.ut.security.client.miniprogram.register;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ut.security.feign.FeignUserService;
import com.ut.security.properties.SecurityConstants;
import com.ut.security.rbac.MyUserEntity;
import com.ut.security.rbac.thirdaccount.miniprogram.MiniProgramUser;
import com.ut.security.rbac.thirdaccount.wechat.ThirdAccountConstant;
import com.ut.security.social.SocialLoginService;
import com.ut.security.social.account.SocialAccountServiceProvider;
import com.ut.security.social.account.SocialAccountServices;
import com.ut.security.support.AES_ECB_128_Service;
import com.ut.security.support.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;

/**
 * 小程序登录过滤链
 * @author litingting
 */
@Slf4j
public class MiniProgramRegisterAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public MiniProgramRegisterAuthenticationFilter() {
		super(new AntPathRequestMatcher(SecurityConstants.MINIPROGRAM_REGISTER_URL, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationServiceException {
		logger.info("===========小程序授权注册流程(frame)===========");

		String loginKey = request.getParameter(SecurityConstants.SOCIAL_ACCOUNT_KEY);
		String encryptedData = request.getParameter(SecurityConstants.ENCRYPTED_DATA);
		String iv = request.getParameter(SecurityConstants.IV);


		SocialLoginService socialService = SpringUtils.getBean(SocialLoginService.class);
		String socialAccountInJson;
		try {
			socialAccountInJson = socialService.getLoginKey(loginKey);
		} catch (Exception e) {
			throw new AuthenticationServiceException("无法找到自定义登录key，请查看是否过期或有效，key=" + loginKey);
		}
		String sessionKey= JSON.parseObject(socialAccountInJson, MiniProgramUser.class).getSessionKey();

		JSONObject obj=getPhoneNumber(sessionKey,encryptedData,iv);
		String phoneNo=obj.get("phoneNumber").toString();

		//获取用户主键
		FeignUserService feignUserService = SpringUtils.getBean(FeignUserService.class);
		AES_ECB_128_Service aes = SpringUtils.getBean(AES_ECB_128_Service.class);
		MyUserEntity userByUsername = feignUserService.getUserByMobile(phoneNo, aes.getSecurityToken());
		if(null == userByUsername)
			userByUsername = feignUserService.mobileRegister(phoneNo,aes.getSecurityToken());

		MiniProgramRegisterAuthenticationToken authRequest = new MiniProgramRegisterAuthenticationToken(userByUsername.getUsername());
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request, MiniProgramRegisterAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}
	private JSONObject getPhoneNumber(String session_key,String encryptedData,String iv ){
		byte[] dataByte = Base64.decode(encryptedData);
		byte[] keyByte = Base64.decode(session_key);
		byte[] ivByte = Base64.decode(iv);
		try {
			int base = 16;
			if (keyByte.length % base != 0) {
				int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
				byte[] temp = new byte[groups * base];
				Arrays.fill(temp, (byte) 0);
				System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
				keyByte = temp;
			}
			// 初始化
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
			parameters.init(new IvParameterSpec(ivByte));
			cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
			byte[] resultByte = cipher.doFinal(dataByte);
			if (null != resultByte && resultByte.length > 0) {
				String result = new String(resultByte, "UTF-8");
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}


}
