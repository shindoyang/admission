/**
 * 
 */
package com.ut.security.support;

import lombok.Data;

/**
 * 简单响应的封装类
 */
@Data
public class ResultResponse {
	private Object msg;
	private String code;

	public ResultResponse() {
	}

	public ResultResponse(Object msg){
		this.msg = msg;
	}

	public ResultResponse(String code, Object msg) {
		this.code = code;
		this.msg = msg;
	}
}
