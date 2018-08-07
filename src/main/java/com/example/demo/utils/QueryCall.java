
 
package com.example.demo.utils;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author ramveersingh
 */
public class QueryCall implements Callable<Long> {
	
	@Autowired
	private HydroPerfUtils utils;
	@Override
	public Long call(){
		return this.utils.queryCall();
	}
}
