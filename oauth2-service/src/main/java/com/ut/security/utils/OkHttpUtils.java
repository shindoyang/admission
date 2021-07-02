package com.ut.security.utils;

import okhttp3.*;

import java.io.IOException;


public class OkHttpUtils {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static OkHttpClient client = new OkHttpClient();


	public static Response delete(String url) throws Exception {
		Request request = new Request.Builder()
				.url(url)
				.delete()
				.build();
		Response response = client.newCall(request).execute();
		return response;
	}

	public static Response access(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		return response;

	}

	public static Response get(String url) throws IOException {
		Request request = new Request.Builder().url(url).get().build();
		Response response = client.newCall(request).execute();
		return response;
	}


//	public String post(HttpMethod method, Map<String, String> param, OkHttpClient client, String url,String token)throws Exception{
//		Request.Builder builder = new Request.Builder();
//
//		if(url != null || !url.equals(""))
//			builder.url(url);
//		if(null != token){
//			builder.header("Authorization", OAuth2AccessToken.BEARER_TYPE + " " + token);
//		}
//		if(param != null){
//			FormBody.Builder body = new FormBody.Builder();
//			for(Map.Entry<String, String> entry : param.entrySet()){
//				body.add(entry.getKey(), entry.getValue());
//			}
//			builder.post(body.build());
//		}
//		Request request = builder.build();
//		Response response = client.newCall(request).execute();
//		return response.body().string();
//	}

	//
	public static Response postJson(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		return response;
	}

	public static Response postForm(String url, FormBody body) throws IOException {

		Request request = new Request.Builder().url(url).addHeader("Authorization", "Basic c3NvLWdhdGV3YXk6c3NvLWdhdGV3YXktc2VjcmV0").post(body).build();
		Response response = client.newCall(request).execute();
		return response;
	}


	public static Response put(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		return response;

	}


}
