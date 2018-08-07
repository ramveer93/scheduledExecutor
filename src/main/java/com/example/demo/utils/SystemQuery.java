
 
package com.example.demo.utils;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import okhttp3.Response;

/**
 * 
 * @author ramveersingh
 */
public class SystemQuery implements Callable<Response> {

	@Autowired
	private HydroPerfUtils utils;
	
	@Override
	public Response call() {
		return this.utils.systemQuery();
	}

}
