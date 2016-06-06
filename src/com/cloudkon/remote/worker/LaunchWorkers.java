package com.cloudkon.remote.worker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class LaunchWorkers {
	public static RemoteSQS taskQueue;
	public static RemoteSQS responseQueue;
	public static Dynamodb dynamodb;
	
	public static void main(String[] args) {
		if(args!=null && args[0].length() >0 && "worker".equalsIgnoreCase(args[0]))
		{
		new WorkerCLI(args).parse();			
		launchRemoteWorkers(args);
		}else{
			System.out.println("Invalid argument");
		}
		
	}
	
	public static void launchRemoteWorkers(String args[]){
		taskQueue = new RemoteSQS(args[2]);
		responseQueue = new RemoteSQS(args[2] + "Reponse");
		taskQueue.initSQSURL();
		responseQueue.initSQSURL();
		
		dynamodb = new Dynamodb();
		dynamodb.createTable(args[2]);	
				
		//get the number of workers
		int numberOfWorkers = Integer.parseInt(args[4]);
		
		String id = "";
		try {
			id = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.out.println("Network address not found.");
		}

		// creates worker threads
		List<RemoteWorkerThread> remoteWorkerThreads = new ArrayList<RemoteWorkerThread>();
		RemoteWorkerThread remoteWorkerThread1 = null;
		for (int i = 0; i < numberOfWorkers; i++) {
			remoteWorkerThread1 = new RemoteWorkerThread();
			remoteWorkerThread1.setName(id+"_"+(i+1));
			remoteWorkerThreads.add(remoteWorkerThread1);
		}

		// start worker threads
		for (RemoteWorkerThread remoteWorkerThread : remoteWorkerThreads) {
			remoteWorkerThread.start();
		}
		
	}
}
