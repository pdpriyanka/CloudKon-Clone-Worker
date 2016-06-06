package com.cloudkon.remote.worker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class RemoteSQS {
	private AmazonSQS amazonSQS;
	private String name;
	private String url;
	private Message message;
	private String credentialFilePath;

	public RemoteSQS(String name) {
		super();
		this.name = name;
	}

	public boolean isSQSExists() {

		try {
			getAmazonSQS().getQueueUrl(new GetQueueUrlRequest(name));
			return true;
		} catch (QueueDoesNotExistException e) {
			System.out.println("Queue " + name + "does not exists.");
			return false;
		}
	}

	public void initSQSURL() {
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(name);
		url = getAmazonSQS().createQueue(createQueueRequest).getQueueUrl();
	}

	public void sendMessage(String message) {
		getAmazonSQS().sendMessage(new SendMessageRequest(url, message));
	}

	public Message ReceivingMessage() {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(url);
		List<Message> messages = getAmazonSQS().receiveMessage(receiveMessageRequest).getMessages();
		if (messages != null && messages.size() > 0) {
			return messages.get(0);
		}
		return null;
	}

	public void deleteMessage(Message message) {
		String messageReceiptHandle = message.getReceiptHandle();
		getAmazonSQS().deleteMessage(new DeleteMessageRequest(url, messageReceiptHandle));
	}

	public void deleteQueue() {
		getAmazonSQS().deleteQueue(new DeleteQueueRequest(url));
	}

	public AmazonSQS getAmazonSQS() {
		if (amazonSQS == null) {
			AWSCredentials awsCredentials = null;
			try {
				// InputStream credentialsFile =
				// getClass().getResourceAsStream("awsSecuCredentials.properties");
				InputStream credentialsFile = new FileInputStream("./awsSecuCredentials.properties");
				awsCredentials = new PropertiesCredentials(credentialsFile);
			} catch (IllegalArgumentException | IOException e) {
				System.out.println("Credential not loaded");
			}
			if (awsCredentials != null) {
				amazonSQS = new AmazonSQSClient(awsCredentials);
				Region usEast1 = Region.getRegion(Regions.US_EAST_1);
				amazonSQS.setRegion(usEast1);
			}
		}
		return amazonSQS;
	}

	public void setAmazonSQS(AmazonSQS amazonSQS) {
		this.amazonSQS = amazonSQS;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public String getCredentialFilePath() {
		return credentialFilePath;
	}

	public void setCredentialFilePath(String credentialFilePath) {
		this.credentialFilePath = credentialFilePath;
	}
	/*
	 * public static void main(String[] args) { RemoteSQS remoteSQS = new
	 * RemoteSQS(args[0]); if(remoteSQS.isSQSExists()){ remoteSQS.initSQSURL();
	 * remoteSQS.sendMessage("0:sleep 0"); Message message =
	 * remoteSQS.ReceivingMessage(); System.out.println(message.getBody());
	 * remoteSQS.deleteMessage(message); message = remoteSQS.ReceivingMessage();
	 * if(message!=null) System.out.println(message.getBody());
	 * 
	 * }
	 * 
	 * 
	 * }
	 */
}
