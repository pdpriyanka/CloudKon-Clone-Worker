package com.cloudkon.remote.worker;

import com.amazonaws.services.sqs.model.Message;

public class RemoteWorkerThread extends Thread {

	@Override
	public void run() {
		String task = null, taskId = null;
		String[] strs = null;
		String taskResponse = null;

		Message message = null;

		while (true) {
			message = LaunchWorkers.taskQueue.ReceivingMessage();
			if (message != null) {

				strs = message.getBody().split(Constants.SEPERATOR);
				if (strs != null && strs.length == 2) {
					taskId = strs[0];
					task = strs[1];
					if (taskId != null) {
						if (!LaunchWorkers.dynamodb.isTaskIdAlreadyPresent(taskId)) {
							if (task != null && task.length() > 5) {
								try {
									LaunchWorkers.dynamodb.addItemInTable(taskId, getName());
									// execute the sleep task
									Thread.sleep(Integer.parseInt(task.replaceAll("[^0-9]+","").trim()));

									// generate the response for successful
									// execution of task
									taskResponse = taskId + Constants.SEPERATOR + 0+ Constants.SEPERATOR+Thread.currentThread().getName();
								} catch (NumberFormatException | InterruptedException | NullPointerException e) {
									//System.out.println("Exception in remote worker thread for task with id " + taskId);
									// generate the response for failed task
									taskResponse = taskId + Constants.SEPERATOR + 1+Constants.SEPERATOR+Thread.currentThread().getName();
								}catch (OutOfMemoryError e) {
									//System.out.println(" Out of memory error in remote worker thread for task with id " + taskId);
									// generate the response for failed task
									taskResponse = taskId + Constants.SEPERATOR + 1+Constants.SEPERATOR+Thread.currentThread().getName();
								}
								
							}
							LaunchWorkers.taskQueue.deleteMessage(message);
							//System.out.println(taskResponse);
							// add response into response queue
							LaunchWorkers.responseQueue.sendMessage(taskResponse);
						}
					}
				}

			}
		}
	}

}
