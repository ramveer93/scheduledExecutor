
 
package com.example.demo.utils;

import java.util.List;

/**
 * 
 * @author ramveersingh
 */
public class MatrixVo {
private String hydroTime;
private String assetTime;
private int runningQueries;
private List<String> systemQueryResp;
/**
 * 
 * @return : 
 */
public String getHydroTime() {
	return this.hydroTime;
}
/**
 * 
 * @param hydroTime : 
 */
public void setHydroTime(String hydroTime) {
	this.hydroTime = hydroTime;
}
/**
 * 
 * @return : 
 */
public String getAssetTime() {
	return this.assetTime;
}
/**
 * 
 * @param assetTime : 
 */
public void setAssetTime(String assetTime) {
	this.assetTime = assetTime;
}

public List<String> getSystemQueryResp() {
	return this.systemQueryResp;
}
public void setSystemQueryResp(List<String> systemQueryResp) {
	this.systemQueryResp = systemQueryResp;
}
public int getRunningQueries() {
	return this.runningQueries;
}
public void setRunningQueries(int runningQueries) {
	this.runningQueries = runningQueries;
}
@Override
public String toString() {
	return "MatrixVo [hydroTime=" + this.hydroTime + ", assetTime=" + this.assetTime + ", runningQueries=" + this.runningQueries //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ ", systemQueryResp=" + this.systemQueryResp + "]"; //$NON-NLS-1$ //$NON-NLS-2$
}
}
