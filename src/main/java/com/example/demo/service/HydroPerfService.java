
 
package com.example.demo.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.utils.HydroPerfException;
import com.example.demo.utils.HydroPerfUtils;
import com.example.demo.utils.MatrixVo;

import okhttp3.Response;

/**
 * 
 * @author ramveersingh
 */
@Service
public class HydroPerfService {
	
	/**
	 * logging object
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HydroPerfUtils utils;
	
	/**
	 * @return : 
	 */
	public void getMatrix() {
		this.log.info("Calling the scheduled task at : "+new Date());
		MatrixVo vo = new MatrixVo();
		Callable<Long> assetTask  = ()->{return this.utils.assetCall();};
		Callable<Long> queryTask  = ()->{return this.utils.queryCall();};
		Callable<Response> sQueryTask  = ()->{return this.utils.systemQuery();};
		ExecutorService service = Executors.newFixedThreadPool(3);
		Future<Long> asset = service.submit(assetTask);
		Future<Long> query = service.submit(queryTask);
		Future<Response> systemQuery = service.submit(sQueryTask);
		try {
		    long assetTime = asset.get();
		    long queryTime = query.get();
		    Response resp = systemQuery.get();
		    String strRes = resp.body().string();
		    if(resp.code()!=200){
		    	this.log.error("Error executing query service : ",strRes); //$NON-NLS-1$
		    	throw new HydroPerfException("Exception calling the query service :"+strRes); //$NON-NLS-1$
		    }
		    JSONObject obj = new JSONObject(strRes);
		    JSONArray dataArray = obj.getJSONArray("data"); //$NON-NLS-1$
		    int runningQueries =  dataArray.length()-1;
		    this.log.info("Asset time is {} query time is {} and system response is {}, running queries: {}",assetTime,queryTime,strRes,runningQueries); //$NON-NLS-1$
		    vo.setAssetTime(Long.toString(assetTime));
		    vo.setHydroTime(Long.toString(queryTime));
		    vo.setRunningQueries(runningQueries);
		    List<String> queries = new ArrayList<>();
		    for(int i=0;i<runningQueries+1;i++){
		    	JSONArray arr= (JSONArray) dataArray.get(i);
		    	String q = (String) arr.get(5);
		    	if(!q.contains("system.runtime.queries")){ //$NON-NLS-1$
		    		this.log.info("query is not a system query so adding to list : "+q); //$NON-NLS-1$
		    		queries.add(q);
		    	}
		    }
		    vo.setSystemQueryResp(queries);
		    this.log.info("vo object is {}",vo.toString()); //$NON-NLS-1$
		} catch (InterruptedException | ExecutionException  | IOException  |JSONException e) {
		    e.printStackTrace();
		    this.log.error("Error getting future value: {}",e.getMessage()); //$NON-NLS-1$
		    throw new HydroPerfException("Error getting future value: {}"+e.getMessage()); //$NON-NLS-1$
		}
		service.shutdown();
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("/Projects/hydra/28/result.txt", true))){ //$NON-NLS-1$
			writer.append("-------------------------------------------------------------time: "+new Date()); //$NON-NLS-1$
			writer.newLine();
			writer.append(vo.toString());
			writer.newLine();
			writer.append("-------------------------------------------------------------"); //$NON-NLS-1$
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			this.log.info("Error writing to a file error is : "+e.getMessage()); //$NON-NLS-1$
		}
		//return vo;
	}

}
