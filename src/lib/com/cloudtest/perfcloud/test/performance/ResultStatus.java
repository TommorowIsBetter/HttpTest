package com.cloudtest.perfcloud.test.performance;

public enum ResultStatus{ 
	PASSED("通过"), FAILED("失败"), UNKNOWN("不确定");
	
	public static String[] NAMES = {"通过", "失败", "不确定"};
	
	private String displayName;
	
	ResultStatus(String displayName){
		this.displayName = displayName;
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public int getIndex(){
		switch(this){
		case PASSED: return 0;
		case FAILED: return 1;	
		case UNKNOWN: return 2;	
		default: return -1;
		}
	}
	
	public static ResultStatus getStatus(int index){
		switch(index){
		case 0: return PASSED;
		case 1: return FAILED;	
		case 2: return UNKNOWN;	
		default: return null;
		}
	}
}