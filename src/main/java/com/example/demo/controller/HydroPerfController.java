/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.example.demo.controller;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.HydroPerfService;
import com.example.demo.utils.MatrixVo;

/**
 * 
 * @author ramveersingh
 */
@RestController
@RequestMapping("v1/hydro")
public class HydroPerfController {

	@Autowired
	private HydroPerfService service;
	
	/**
	 * logging object
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/getMatrix", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getMatrix(){
		this.log.info("getMatrix (): starting the execution of getMatrix");
		try {
			//Callable<MatrixVo> task = ()->{return this.service.getMatrix();};
			this.log.info("getMatrix (): scheduling the task to get metrix from service at: "+new Date());
			Runnable r = ()->{this.service.getMatrix();};
			ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
			scheduled.scheduleWithFixedDelay(r, 1, 5, TimeUnit.MINUTES);
			this.log.info("getMatrix (): Task scheduled at : "+new Date());
			//MatrixVo mvo = future.get();
			//vo = this.service.getMatrix();
			//scheduled.shutdown();
			//this.log.info("getMatrix (): schedular shutdown at : "+new Date());
		} catch(Exception e){
			return new ResponseEntity<Object>("Error executing the request: "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
}
