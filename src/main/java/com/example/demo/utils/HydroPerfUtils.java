/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.example.demo.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * @author ramveersingh
 */
@Service
public class HydroPerfUtils {
	/**
	 * This has the Environment object to retrive
	 */

	@Autowired
	private Environment environment;

	/**
	 * okHttpClient : 
	 */
	OkHttpClient okHttpClient;


	/**
	 * logging object
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * This method will give the value from properties file for given key
	 * 
	 * @param key
	 *            will be passed to get the value from properties file
	 * @return the property value string
	 * @throws IOException
	 *             throws the data source exception
	 */
	public String getPropertiesValue(String key){
		return this.environment.getProperty(key);

	}

	/**
	 * @param key
	 *            :
	 * @param clas
	 *            :
	 * @return :
	 * @throws IOException
	 *             :
	 */
	public <T> T getPropertiesValue(String key, Class<T> clas) throws IOException {
		return this.environment.getProperty(key, clas);
	}
	
	
	
	/**
	 * getHttpClient
	 *
	 * @return : OkHttpClient
	 */
	public OkHttpClient getHttpClient() {
		OkHttpClient client = null;
		if (null != this.okHttpClient) {
			return this.okHttpClient;
		}
		try {
			final TrustManager[] trustAllCerts = getTrustManager();
			final SSLContext sslContext = SSLContext.getInstance("SSL"); //$NON-NLS-1$
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory);
			builder.hostnameVerifier(getHostnameVerifier());
			builder.connectTimeout(200, TimeUnit.SECONDS);
			builder.writeTimeout(200, TimeUnit.SECONDS);
			builder.readTimeout(200, TimeUnit.SECONDS);
			builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("sjc1intproxy01.crd.ge.com", 8080))); //$NON-NLS-1$
			client = builder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}

	private TrustManager[] getTrustManager() {
		TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// accept all clients
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// accept all clients
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}
		} };
		return trustManager;
	}

	private HostnameVerifier getHostnameVerifier() {
		HostnameVerifier hostnameVerifier = (hostname, session) -> true;
		return hostnameVerifier;
	}

	

	

	/**
	 * This method makes call to external API
	 * 
	 * @param request
	 *            will be the okhttp request object
	 * @return the okhttp request
	 * @throws IOException
	 *             will be thrown if exception occurred while calling the rest
	 *             API
	 * @throws HydroPerfException 
	 */
	public Response makeHttpCall(Request request) {
		OkHttpClient client = getHttpClient();
		this.log.info("DataSourceUtils.makeHttpCall() called with url: {} header: {} method: {}",request.url(), request.headers(),request.method()); //$NON-NLS-1$
		try {
			return client == null ? null : client.newCall(request).execute();
		} catch (Exception e) {
			this.log.error("makeHttpCall() Exception while calling the external api for url {} and exception {}", //$NON-NLS-1$
					request.url(), e);
			this.log.info("makeHttpCall {}", HydroPerfConstants.METHOD_EXIT); //$NON-NLS-1$
			throw new HydroPerfException("Exception calling the api :  "+e.getMessage()); //$NON-NLS-1$
		}
	}

	

	/**
	 * This method will make a get request to catalog services to get the rest
	 * end point meta based on tableName
	 * 
	 * @param tableName
	 *            will be the input to be appended with catalog url
	 * @param catalogName
	 *            is to identify catalog name
	 * @param authorization
	 *            : authorization token to make call with catalog
	 * @param tenant
	 *            : tenant
	 * @return the okhttp response object which will be used to get the body
	 * @throws HydroPerfException 
	 * @throws IOException 
	 * @throws Exception
	 *             the DataSource exception
	 */
	public long assetCall() {
		long start = System.currentTimeMillis();
		this.log.debug("makeHttpGet() {}", HydroPerfConstants.METHOD_START); //$NON-NLS-1$
		Response res = null;
		Map<String, String> headers = new HashMap<>();
		headers.put(HydroPerfConstants.AUTHORIZATION, getPropertiesValue(HydroPerfConstants.AUTHORIZATION));
		headers.put(HydroPerfConstants.TENANT,getPropertiesValue(HydroPerfConstants.TENANT) );
		try {
			String url = getPropertiesValue(HydroPerfConstants.URL);
			Headers headerbuild = Headers.of(headers);
			this.log.info("makeHttpGet()  calling the catalog api with url : {} , header map as {} ",url, headers); //$NON-NLS-1$
			Request request = new Request.Builder().url(url).headers(headerbuild).build();
			res = makeHttpCall(request);
			String r = res.body().string();
			//this.log.info("asset call result: "+r); //$NON-NLS-1$
			if(res.code()!=200 && res.code()!=206){
				this.log.info("Not able to call the asset serivce error is{}  : ",r); //$NON-NLS-1$
				return 0;
			}
		} catch (Exception ex) {
			this.log.error("makeHttpGet() {} exception while calling assets {}", //$NON-NLS-1$
					HydroPerfConstants.EXCEPTION_OCCURED, ex);
			this.log.debug("makeHttpGet() {} ", HydroPerfConstants.METHOD_EXIT); //$NON-NLS-1$
			throw new HydroPerfException("Error occured while calling assets api:"+ex.getMessage()); //$NON-NLS-1$
		}
		this.log.debug("makeHttpGet() {} ", HydroPerfConstants.METHOD_EXIT); //$NON-NLS-1$
		long end = System.currentTimeMillis();
		return end-start;
	}
	
	/**
	 * @return : 
	 * @throws HydroPerfException : 
	 */
	public long queryCall() throws HydroPerfException{
		this.log.info("queryCall(): calling the query service"); //$NON-NLS-1$
		long start = System.currentTimeMillis();
		Response res = null;
		try{
			Map<String, String> headers = new HashMap<>();
			headers.put(HydroPerfConstants.AUTHORIZATION, getPropertiesValue(HydroPerfConstants.AUTHORIZATION));
			headers.put(HydroPerfConstants.TENANT,getPropertiesValue(HydroPerfConstants.TENANT) );
			Headers headersbuild = Headers.of(headers);
			String url = getPropertiesValue("query_url"); //$NON-NLS-1$
			JSONObject content = new JSONObject();
		 	content.put("query_string", "select * from gl_fetchtags where assetUri='/sites/8866ac60-8298-3c50-a97b-defe90a57c92'"); //$NON-NLS-1$ //$NON-NLS-2$
			MediaType json = MediaType.parse(HydroPerfConstants.APPLICATION_JSON_CHARSET_UTF_8); 
			RequestBody body = RequestBody.create(json, content.toString());
			Request req = new Request.Builder().url(url).post(body).headers(headersbuild).build();
			res = makeHttpCall(req);
			this.log.info("queryCall(): res from query service {}",res); //$NON-NLS-1$
			if(res.code()!=200){
				this.log.info("Not able to call the query serivce error : ",res); //$NON-NLS-1$
				return 0;
			}
		}catch(Exception e){
			throw new HydroPerfException("Exception occured while executing query : "+e.getMessage()); //$NON-NLS-1$
		}
		long end = System.currentTimeMillis();
		return end-start;
	}

	/**
	 * 
	 * @return : 
	 * @throws HydroPerfException : 
	 */
	public Response systemQuery(){
		this.log.info("systemQuery(): calling the system query service"); //$NON-NLS-1$
		Response res = null;
		try{
			Map<String, String> headers = new HashMap<>();
			headers.put(HydroPerfConstants.AUTHORIZATION, getPropertiesValue(HydroPerfConstants.AUTHORIZATION));
			headers.put(HydroPerfConstants.TENANT,getPropertiesValue(HydroPerfConstants.TENANT) );
			Headers headersbuild = Headers.of(headers);
			String url = getPropertiesValue("query_url"); //$NON-NLS-1$
			JSONObject content = new JSONObject();
		 	content.put("query_string", "select * from system.runtime.queries where state = 'RUNNING'"); //$NON-NLS-1$ //$NON-NLS-2$
			MediaType json = MediaType.parse(HydroPerfConstants.APPLICATION_JSON_CHARSET_UTF_8);
			RequestBody body = RequestBody.create(json, content.toString());
			Request req = new Request.Builder().url(url).post(body).headers(headersbuild).build();
			this.log.info("systemQuery () Request:  {}",req); //$NON-NLS-1$
			res = makeHttpCall(req);
			this.log.info("systemQuery (): response from system query {}",res); //$NON-NLS-1$
		}catch(Exception e){
			throw new HydroPerfException("Exception occured while executing query : "+e.getMessage()); //$NON-NLS-1$
		}
		return res;
	}

		


	

	

	/**
	 * 
	 * @return the resource bundle corresponding to local defined in properties
	 *         file
	 */
	public ResourceBundle getResourceBundle() {
		this.log.debug("getResourceBundle() {} ", HydroPerfConstants.METHOD_START); //$NON-NLS-1$
		ResourceBundle resourceBundle = null;
		resourceBundle = ResourceBundle.getBundle(HydroPerfConstants.MESSAGE_PROPERTIES_FILE, Locale.getDefault(),
				this.getClass().getClassLoader());
		return resourceBundle;
	}


	
	/**
	 * @param message : 
	 * @param code : 
	 * @param detailedMsg : 
	 * @return : the string form of JSON
	 */
	public String constructOkHttp3RespJSon(String message, String code, String detailedMsg){
		JSONObject obj = new JSONObject();
		try{
		     obj.put(HydroPerfConstants.CODE, code);
		     obj.put(HydroPerfConstants.MESSAGE, message);
		     obj.put(HydroPerfConstants.DETAILED_MSG, detailedMsg);
		     return obj.toString();
		}catch(JSONException exp){
		  this.log.error("constructOkHttp3RespJSon(): Error creating okhttp3 response json {}",exp.getMessage()); //$NON-NLS-1$
		}
		return obj.toString();
	}
	
	
	
}
