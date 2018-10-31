package com.cloudtest.testdriver;

import com.cloudtest.perfcloud.test.performance.ResultStatus;

public class TestSessionListenerImpl implements TestSessionListener{
	private String sessionId;
	
	public TestSessionListenerImpl(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Override
	public void onTestBegin() {
		System.out.println("test_begin(" + sessionId + ")");
	}

	@Override
	public void onTestEnd(ResultStatus status, String error, 
			String log, long sentBytes,	long recvBytes) {
		System.out.println("test_end(" + sessionId +", " + status + ", " + error + ", " 
			+ log+ ", " + sentBytes + ", " + recvBytes + ")");
	}

	@Override
	public void onActionBegin(String threadId, String action) {
		System.out.println("action_begin(" + sessionId +", " + threadId + ", " + action + ")");
	}

	@Override
	public void onActionEnd(String threadId, String action, ResultStatus status, String error,
			String log, long sentBytes, long recvBytes) {
		System.out.println("action_end(" + sessionId + ", " + threadId  + ", " + action + ", " 
			+ status + ", " + error + ", " + log+ ", " + sentBytes + ", " + recvBytes + ")");
	}

	@Override
	public void onRendezvous(String rendezvousName) {
		System.out.println("rendezvous(" + sessionId + ", " + rendezvousName + ")");
	} 
}
