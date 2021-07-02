package com.ut.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChengLin
 * @creat 2020/11/18
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomExceptionDTO {
	private String timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
}
