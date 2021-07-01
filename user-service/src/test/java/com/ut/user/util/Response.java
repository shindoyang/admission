package com.ut.user.util;

import lombok.Data;

@Data
public class Response {
	int status;
	Object response;

	public Response() {
	}

	public Response(int status, Object response){
		this.status = status;
		this.response = response;

	}

}
