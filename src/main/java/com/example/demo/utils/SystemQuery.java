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
