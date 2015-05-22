
package gr.uth.inf.ce325.http_server;
import gr.uth.inf.ce325.xml_parser.*;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.io.*;


public class serverCreator {
	private String xmlInp; //input to set server from
	private List <String> denyIpList; //List of Banned ipz
	private logFiles LOG;
	
	
	
	
	//constructor
	public serverCreator(){
		LOG =  new logFiles();
		this.denyIpList = new LinkedList<>();
	}
	
	//methods
	
	
	//creates server given the input xml file
	public Server createServer(String xI) throws IOException{
		
		//start by constructing one
		Server newServer = new Server();
		this.xmlInp = xI;
		
		//create a document to read from
		DocumentBuilder docB = new DocumentBuilder(); //build new document
		Document config;
		config = docB.getDocument(xmlInp);//parse input and store result to document
		
		Node Root = config.getRootNode();
		populateNewServer(Root, newServer);//add info to server
		checkDirs (newServer); //prevent fileNotFound errors
		newServer.setLOG(LOG);//Sets LOG to Server
		newServer.setDeniedList(denyIpList);//Set denied list to server
		
		//System.out.println (newServer.toString()); //Use it for Server info
		
		
		return (newServer);
	}
	
	
	//Recursively set basic server values
	private void populateNewServer(Node current,Server newServer) throws IOException{
		
		List <Node>Children;
		
		
		//read value names and assign them to the appropriate field
		if(current.getName().contains("root")){
			
			
			if (System.getProperty("os.name").startsWith("Windows")) {
				newServer.setRootDirectory(current.getFirstAttribute().getValue().replace("/","\\"));          //replace with windows file system backslashes
			} else {
				newServer.setRootDirectory(current.getFirstAttribute().getValue().replace("\\","/"));          //replace with windows file system backslashes
			} 
			
			
		}
		else if (current.getName().contains("php")){
			if (current.getText().contains("yes"))
				newServer.runsPHP();
		}
		else if(current.getName().equals("listen")){
			
			newServer.setListeningPort(Integer.parseInt(current.getFirstAttribute().getValue()));
		}
		else if(current.getName().equals("statistics")){
			newServer.setStatisticsPort(Integer.parseInt(current.getFirstAttribute().getValue()));
		}
		else if(current.getName().equals("access")){
			
			if (System.getProperty("os.name").startsWith("Windows")) {
				LOG.setAccess(current.getFirstAttribute().getValue().replace("/","\\"));          //replace with windows file system backslashes
			} else {
				LOG.setAccess(current.getFirstAttribute().getValue().replace("\\","/"));          //replace with unix file system backslashes
			} 
			
		}   
		else if(current.getName().equals("error")){
			
			if (System.getProperty("os.name").startsWith("Windows")) {
				LOG.setError(current.getFirstAttribute().getValue().replace("/","\\"));          //replace with windows file system backslashes
			} else {
				LOG.setError(current.getFirstAttribute().getValue().replace("\\","/"));          //replace with unix file system backslashes
			} 
			
		}
		
		else if(current.getName().equals("ip")){
			denyIpList.add(current.getText().trim());
		}
		
		
		Children = current.getChildren();
		//for every children do the same
		
		
		for (Node temp : Children){
			populateNewServer (temp, newServer);
		}
		
		
		
		
	}
	
	//check if folders exist. if not create them
	private void checkDirs(Server s){
		
		//check for root dir
		File r = new File(s.getRoot());
		if (!r.exists()){
			if (!r.mkdirs()){
				System.out.println("Error Creating Files. Shutting Down Program!!!");
				System.exit(1);
			}
		}
		//check for access file
		File access = new File(LOG.getAccessF());
		File acFold = new File(access.getParent());
		System.out.println(acFold.toString());
		if (!acFold.exists()){
			if (!acFold.mkdirs()){
				System.out.println("Error Creating Files. Shutting Down Program!!!");
				System.exit(1);
			}
		}
		//check for error file
		File error = new File(LOG.getErrorF());
		File erFold = new File(error.getParent());
		if (!erFold.exists()){
			if (!erFold.mkdirs()){
				System.out.println("Error Creating Files. Shutting Down Program!!!");
				System.exit(1);
			}
		}
	}
	
}
