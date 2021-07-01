package com.ut.user.vo;

import lombok.Data;

/**
 * @author litingting
 * @date 2019-3-29
 */
@Data
public class BindStatuVO {
	/**
	 * 绑定微信的标识
	 */
	private Boolean wechat;

	/**
	 * 绑定微博的标识
	 */
	private Boolean weibo;

	/**
	 * 绑定QQ的标识
	 */
	private Boolean qq;

	public BindStatuVO(){}

	public BindStatuVO(Boolean wechat, Boolean weibo, Boolean qq){
		this.wechat = wechat;
		this.weibo = weibo;
		this.qq = qq;
	}
}
