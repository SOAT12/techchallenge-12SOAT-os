package com.fiap.soat12.os.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class SessionToken {
	
	private HashMap<String, String> transactionSessionMap = new HashMap<String, String>();
	
	private HashMap<String, String> sessionTransactionMap = new HashMap<String, String>();

	
	public void addTransactionAndSession(String sessionId, String transactionId) {
		this.sessionTransactionMap.put(sessionId, transactionId);
		this.transactionSessionMap.put(transactionId, sessionId);
	}

	public void removeTransactionAndSession(String sessionId) {
		String transactionId = sessionTransactionMap.get(sessionId);
		this.sessionTransactionMap.remove(sessionId);
		this.transactionSessionMap.remove(transactionId);
	}
	
	public HashMap<String, String> getTransactions(){
		return transactionSessionMap;
	}
	
	public HashMap<String, String> getSessions(){
		return sessionTransactionMap;
	}

	public String GetSessionId(String transactionId) {
		return this.transactionSessionMap.get(transactionId);
	}
	
}
