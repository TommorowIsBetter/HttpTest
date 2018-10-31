package com.cloudtest.testdriver;

import com.cloudtest.perfcloud.test.performance.ResultStatus;

public interface TestSessionListener {
	public void onTestBegin();
	
	public void onTestEnd(ResultStatus status, String error, 
			String log, long sentBytes, long recvBytes);
	
	public void onActionBegin(String threadId, String action);
	
	public void onActionEnd(String threadId, String action, ResultStatus status, 
			String error, String log, long sentBytes, long recvBytes);
	
	public void onRendezvous(String rendezvousName);
}
