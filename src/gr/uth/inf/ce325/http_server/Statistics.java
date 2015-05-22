package gr.uth.inf.ce325.http_server;

import java.text.DateFormat;
import java.util.*;

public class Statistics {
	//to-do-with-time vars
	private long timerStart;
	private DateFormat dateEN;
	private Date start_time;
	private Locale localeEN;
	//number of requests
	private int totalRequests; 
	//total requests failed
	private int failReq;
	//more specifically
	private int notAllowed;
	private int success;
	private int badRequest;
	private int notFound;
	private int interError;
	//average serve time vars
	private double avSerTime;
	private long totSerReqTime;
	//Server Name
	private String serverName;
	
	
	//CONSTRUCTORS
	public Statistics(){
		//set time format
		start_time = new Date();
		localeEN = Locale.ENGLISH;
		dateEN = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,localeEN);
		
		//start timer
		
		timerStart = System.currentTimeMillis()/1000;
		
		//get starting time
		start_time = new Date();
		//init other values
		totalRequests = 0 ;
		failReq = 0;
		success = 0;
		badRequest = 0;
		notFound = 0;
		interError = 0;
		avSerTime = 0;
		notAllowed = 0 ;
	}
	
	public Statistics(String sN){
		//set time format
		start_time = new Date();
		localeEN = Locale.ENGLISH;
		dateEN = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,localeEN);
		
		//start timer
		
		timerStart = System.currentTimeMillis()/1000;
		
		//get starting time
		start_time = new Date();
		//init other values
		totalRequests = 0 ;
		failReq = 0;
		success = 0;
		badRequest = 0;
		notFound = 0;
		interError = 0;
		avSerTime = 0;
		notAllowed = 0;
		serverName = sN;
	}
	
	//SET METHODS
	//Sets server Name
	public void setName (String sN){ serverName = sN; }
	//increments total Requests per one
	public void incrRequest(){ totalRequests++; }
	//increments total fail requests per one
	public void incrFail() { failReq++;   }
	//these methods increment each variable per one when called
	//used for updating info about statistics
	public void incrSuccessR(){ success++; incrRequest(); }
	
	public void incrBadReq(){ badRequest++; incrRequest(); incrFail();}
	
	public void incrNotFound(){ notFound++; incrRequest(); incrFail();}
	
	public void incrIntError(){ interError++; incrRequest(); incrFail();}
	
	public void incrMethNotAll(){notAllowed++;incrRequest();incrFail();}
	
	//everytime a request is made and served we calculate the average serve time
	public void ServeTime(long lastServe){
		//add to the total requests time the last requests time
		totSerReqTime += lastServe;
		if (totalRequests != 0){
			//calculate average time
			avSerTime = totSerReqTime/totalRequests;
		}else {avSerTime = 0;}
		
	}
	//GET METHODS
	//find total time running
	public String getTimeRunning(){
		//find current time
		long isRunning = System.currentTimeMillis()/1000 - timerStart;
		
		long hours = isRunning/3600;
		long minutes = (isRunning%3600)/60;
		long seconds = isRunning%60;
		
		return ("Hours: " + hours +" Minutes: " +minutes+" Seconds: " + seconds);
		
	}
	
	
	
	//to String method
	//prints the string representation of statistics which will be printed in
	//clients screen
	@Override
	public String toString(){
		StringBuilder SB = new StringBuilder();
		
		SB.append("Server : ").append(serverName);
		SB.append("\nStarted : ").append(dateEN.format(start_time));
		SB.append("\nRunning : ").append(getTimeRunning()).append("\n\n");
		SB.append("Number of total Requests : ").append(totalRequests);
		SB.append("\nAverage Serving Time : ").append(avSerTime).append("s");
		SB.append("\nSuccesfull Requests : ").append(success);
		SB.append("\nFailed Requests : ").append(failReq);
		SB.append("\n\t#Bad Requests : ").append(badRequest);
		SB.append("\n\t#File Not Found : ").append(notFound);
		SB.append("\n\t#Internal Server Error : ").append(interError);
		SB.append("\n\t#Method Not Allowed : ").append(notAllowed);
		
		return (SB.toString());
	}
	
	
	
}
