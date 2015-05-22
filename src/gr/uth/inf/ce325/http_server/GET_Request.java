package gr.uth.inf.ce325.http_server;


import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;


public final class GET_Request {
	//Basic vars
	private String rootDir;
	//request vars
	private String fileToGet;
	private List <String> acceptLang;
	private String Host;
	private String Connection;
	private String Version;
	private String userAgent;
	//response vars
	private boolean goFurther; //see if we got what we need
	private boolean GetError; //if we got an error
	private String Status;  //contains the server status
	private String GetDate; //storing the date
	private String ServerName; //Server's name
	private String lastMod;   //date of last modification
	private long contLength; //length of content
	private String contType; //content type
	private boolean runphp;
	private boolean hasparams;
	
	//Constructors
	public GET_Request(String ServerName , String request, String rD,boolean r){
		//set basic values to variables
		Connection = "close";  //always set to close
		GetError = false;       //initializing before testing
		this.ServerName = ServerName;
		rootDir = rD;
		runphp=r;
		userAgent="Undefined";
		goFurther = false;      //initializing 
		//check if request is ok and store variables to the right fields
		//System.out.println("Cool SO far");
		buildRequest (request); 
		
		
	}
	
	public GET_Request(String ServerName ,  String rD, boolean r){
		//set basic values to variables
		Connection = "close";       //always set to close
		GetError = false;          //initializing before testing
		this.ServerName = ServerName;
		rootDir = rD;
		runphp=r;
		userAgent="Undefined";
		goFurther = false;      //initializing 
		
		
	}
	//Methods
	
	
	public void buildRequest (String request){
		
		parseRequest(request);
		if (goFurther){ //if we got the command we need
			try{  
				checkRequest();   //check
			}
			catch (IOException ie){
				
				System.out.println(ie.getCause());
				Status = "500 Internal Server Error";
				GetError = true;      //setting to true cause there's an error
			}
		}
		
		
	}
	
	
	//start parsing of request
	public void parseRequest(String request){
		
		boolean invalidCom = true;
		
		//setting patterns and matches
		Pattern getF = Pattern.compile("GET(.*)HTTP/");
                Pattern notF = Pattern.compile("(.*)HTTP/");
		Pattern getV = Pattern.compile("HTTP/\\s*(.*)\\s*(\n|$)");
		Pattern getH = Pattern.compile("Host:\\s*(.*)\\s*(\n|$)");
		Pattern getL = Pattern.compile("Accept-Language:(.*);");
		Pattern getC = Pattern.compile("Connection:\\s*(.*)\\s*(\n|$)");
		Pattern getU = Pattern.compile("User-Agent:\\s*(.*)\\s*(\n|$)");
		Matcher getM1 = getF.matcher(request);
		Matcher getM2 = getV.matcher(request);
		Matcher getM3 = getH.matcher(request);
		Matcher getM4 = getL.matcher(request);
		Matcher getM5 = getC.matcher(request);
		Matcher getM6 = getU.matcher(request);
                Matcher getM7 = notF.matcher(request);
		
		//if there's a get request
		if (getM1.find()){
			fileToGet = getM1.group(1);
			fileToGet = fileToGet.trim();
			invalidCom = false;
		}else if(getM7.find()){
                    Status = "405 Method Not Allowed";
                    GetError = true;
                    invalidCom = false;
		}
		
		//if theres a version number
		if (getM2.find()){
			Version = getM2.group(1);
			Version = Version.trim();
			invalidCom = false;
			if (Version.equals("1.0")){
				goFurther = true;
			}
		}
		
		//if there's a host address
		if (getM3.find()){
			Host = getM3.group(1);
			Host = Host.trim();
			goFurther = true;
			invalidCom = false;
		}
		
		//if there's a language config
		if (getM4.find()){
			acceptLang = Arrays.asList(getM4.group(1).split(","));
			invalidCom = false;
		}
		
		//if there's a connection config
		if (getM5.find()){
			Connection = getM5.group(1);
			Connection = Connection.trim();
			invalidCom = false;
		}
		
		//if there's a user-agent config
		if (getM6.find()){
			userAgent = getM5.group(1);
			userAgent = userAgent.trim();
			invalidCom = false;
		}
		
		if (invalidCom)
		{
			
			Status = "400 Bad Request"; 
			GetError = true;
			
		}
		//connection, lang etc are for possible not ending expansion, we ignore them in this version
		
	}
	
	public boolean isFinished(){return goFurther;}     //if its ok to continue
	
	
	//further checks request
	public void checkRequest() throws IOException{
		fileToGet=fileToGet.replaceAll("\\s+","");      //remove whitespaces
		
		//fileToGet=".\\" + fileToGet;
		if (System.getProperty("os.name").startsWith("Windows")) {
			fileToGet=fileToGet.replace("/","\\");          //replace with windows file system backslashes
			
		} else {
			fileToGet=fileToGet.replace("\\","/");          //replace with unix file system backslashes
			
		} 
		
		
		
		File root = new File(rootDir);                  //get the root dir 
		fileToGet = root + fileToGet;                   //make file's path absolute by making it root+relative path
		
		
		File f = new File(fileToGet);                   //get the file from path
		
		//if php file and if server supports php exec
		if (fileToGet.contains(".php")){
			
			if (runphp){
				if (fileToGet.contains(".php"))
					hasparams=true;
			}else{
				Status = "405 Method Not Allowed";
				GetError = true;
				
			}
			
		}//else if file doesn't exist
		else if  (!f.exists()){
				Status =  "404 Not Found";
				GetError = true;
		}
		
		//if request is a dir
		if(f.isDirectory() && !GetError) { 
			fileToGet=fileToGet+"//index.html";//check if html file exists
			
			f = new File(fileToGet);
			if (f.exists()){
				
				Status = "200 OK";
				
			}
			else{//if it doesn't check for html
				
				fileToGet=fileToGet.replace(".html", ".htm");
				f = new File(fileToGet);
				if (f.exists()){//same here
					
					Status = "200 OK";
					
				}
				else{//if it doesn't check for php
					fileToGet=fileToGet.replace(".htm", ".php");
					f = new File(fileToGet);
					if (f.exists()){
						if (runphp){
							
							Status = "200 OK";
							
						}
						else {
							Status = "400 Method Not Allowed";
							GetError = true;
							
							return;
						}
					}//Create HTML
					else{
						fileToGet=fileToGet.replace(".php", ".html");
						buildHTML();
						Status = "200 OK";
					}
				}
			}
		}
		//else if file is indeed a file
		else if(f.exists() && !f.isDirectory() && !GetError) { 
			
			//find date
			//also do some logging here
			DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
			Date dateobj = new Date();
			GetDate =  df.format(dateobj);
			//find mime type, length, last mod date etc
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String mimeType = fileNameMap.getContentTypeFor(fileToGet);
			contType =  mimeType;
			contLength = f.length();
			lastMod = df.format(f.lastModified());
			
			Status = "200 OK";
			
		}
		
		//if it's a unix system check again to see if everything's ok with path
		if (!System.getProperty("os.name").startsWith("Windows")) {
			
			fileToGet = checkUnix(fileToGet);
					
		}
		
	
	}
	
	
	
	
	//Basic Get Methods
	public String getDate(){
		return GetDate;
	}
	
	public String getModDate(){
		return lastMod;
	}
	
	public String getVersion(){
		return Version;
	}
	
	public String getAgent(){
		return userAgent;
	}
	
	public String filerequest(){
		return fileToGet;
	}
	
	//sets status
	public void setStatus(String s){
		Status = s;
		if (!Status.contains("200")){
			
			GetError = true;
			
		}
	}
	
	
	//returns Status and error state
	public String getStatus(){ return Status;} 
	
	//checks if there is an error
	public boolean existsError(){ return GetError;}
	
	public boolean isPHPar(){return hasparams;}
	
	//create html representation of file
	private void buildHTML() throws IOException{
		StringBuilder hmBl = new StringBuilder();
		fileToGet=fileToGet.replace("index.html", "");
		File createH = new File(fileToGet);
		File lFiles[] = createH.listFiles();
		
		
		//Give the appropriate form to server's appearance
		hmBl.append("<html>\n" +
		"<head>\n<meta charset=\"UTF-8\">" +
		"<style>body{ font-family:Tahoma;} .size, .date {padding: 0 30px} h1.header {color: red; vertical-align: middle;} td{ padding:10px;}</style><title>CE325 HTTP Server G13</title>"
		+ "<h1 class=\"header\">");
		//always check for OS File Must be compatible with all products
		if (System.getProperty("os.name").startsWith("Windows")) {
			hmBl.append("<img src=\"\\images/head.jpg\" width=100px/> CE325 HTTP Server G13</h1><h2>Current Folder: ");
		}else{
			hmBl.append("<img src=\"/images/head.jpg\" width=100px/> CE325 HTTP Server G13</h1><h2>Current Folder: ");
		}
		
		//check for double backslash 
		String currF = fileToGet;
		
		//do some folder magic
		currF = currF.replace("index.html", "");
		File currFold = new File(currF);
		
		//continue building html
		hmBl.append(currFold.getName()).append("/\n</h2></head>\n<body><table><tr><th></th><th>Filename</th><th>Size</th>"
		+ "<th>Last Modified</th></tr>");
		
		//if not root print parent
		if (!currFold.getCanonicalPath().equals(rootDir)){
			
			String prev = currFold.getParent();
			//System.out.println("PAPA!!!" + prev);
			
			if (System.getProperty("os.name").startsWith("Windows")) {
				prev = prev.replace(rootDir ,"\\");
				prev = "\\.."+prev;
			}else{
				prev = prev.replace(rootDir ,"/");
				prev = "/.."+prev;
			}
			
			
			hmBl.append("<tr><td></td><td><a href=").append(prev);
			if (System.getProperty("os.name").startsWith("Windows")) {
				hmBl.append("><img src=\\images\\").append("back.gif");}
				else{
					hmBl.append("><img src=/images/").append("back.gif");
				}
				hmBl.append("> Parent Directory</a></td><td></td><td></td></tr>");
		}
		
		
		//For every file find appropriate icon to link it with and fill in basic info
		String fPath;
		for (File f : lFiles){
			
			
			
			hmBl.append("<tr><td></td><td><a href=");
			fPath = f.getCanonicalPath().replace(rootDir ,"");
			if (System.getProperty("os.name").startsWith("Windows")) {
				hmBl.append(fPath).append("><img src=\\images\\");
			}else{
				hmBl.append(fPath).append("><img src=/images/");
			}
			if (f.isDirectory()){
				hmBl.append("folder.gif");
				
				
			}
			else if (f.getAbsolutePath().contains(".ico")){
				hmBl.append("unknown.gif" );
			}
			
			else if (f.getPath().contains(".php")){
				if (runphp){
				}
			}
			
			else {
				FileNameMap fileNameMap = URLConnection.getFileNameMap();
				String mimeType = fileNameMap.getContentTypeFor(f.getCanonicalPath());
				
				
				if (mimeType.contains("image")){
					hmBl.append("image2.gif");
				}
				else if (mimeType.contains("text")){
					hmBl.append("image1.gif");
				}
				else if (mimeType.contains("video")){
					hmBl.append("image3.gif");
				}
				else if (mimeType.contains("audio")){
					hmBl.append("image4.gif");
				}
				else if (mimeType.contains("application")){
					hmBl.append("application.png width = 20px " );
				}
				else{
					hmBl.append("unknown.gif" );
				}
			}
			hmBl.append("> ").append(f.getName()).append("</a></td><td>");
			hmBl.append(f.length()).append("B").append("</td><td>");              
			
			DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
			String mod = df.format(f.lastModified());
			hmBl.append(mod).append("</td></tr>");
			
		}
		
		hmBl.append("</table></body></html>");
		

		String toPrint= hmBl.toString();
		if (System.getProperty("os.name").startsWith("Windows")) {
			fileToGet = fileToGet + "\\index.html";  }
			else{
				fileToGet = fileToGet + "/index.html";
				fileToGet = checkUnix (fileToGet);
				toPrint = checkUnix(toPrint);
			}
			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileToGet, true)))) {
				out.println(toPrint);
			}catch (IOException e) {
				throw e;//Don't forget to catch it
			}
			
			
	}
	
	
	
	
	
	//builds and returns the HTTP response to the given request
	public String getResponse(){
		StringBuilder reqBuild = new StringBuilder();
		
		reqBuild.append("HTTP/").append(Version).append(" ").append(Status).append("\n");
		if (GetError){
			reqBuild.append("\n");
			return (reqBuild.toString());
		}
		
		reqBuild.append("Date: ").append(GetDate).append("\n");
		
		reqBuild.append("Server: ").append(ServerName).append("\n");
		
		reqBuild.append("Last Modified: ").append(lastMod).append("\n");
		
		reqBuild.append("Content Length: ").append(contLength).append("\n");
		
		reqBuild.append("Connection: ").append(Connection).append("\n");
		
		reqBuild.append("Content Type: ").append(contType).append("\n");
		
		reqBuild.append("User agent: ").append(userAgent).append("\n");
		
		reqBuild.append("\n");
		
		
		
		return (reqBuild.toString());
	}
	
	
	//try to include only one "/" per request if unix
	private String checkUnix(String request){
		
		while (request.contains("//")){
			
			request = request.replace("//","/");
			
		}
		
		return request;
	}
	
	
	
	
	
	//method to prepare the file requested for sending
	public byte[] sendFile() throws IOException{
		RandomAccessFile f = new RandomAccessFile(fileToGet, "r");
		
        try {
            // Get and check length
            byte[] toSend = new byte[(int)f.length()];
            f.readFully(toSend);
            return toSend;
        } finally {
            f.close();
        }
    	
	}
		
	//Reads file and returns the file after changing parameters if php
	public byte[] parsePHP() throws FileNotFoundException,IOException{
		byte [] finalForm;// Final byte form
		String phpCont = ""; // String to read from file
		String phpParams = fileToGet.substring(fileToGet.lastIndexOf("?") + 1);
		fileToGet = fileToGet.substring(0, fileToGet.lastIndexOf("?"));
		
		//contains <Parameters, Values> useful for parsing
		Map <String,String> params = new LinkedHashMap<String, String>();	
		
		Pattern getPars = Pattern.compile("User-Agent:\\s*(.*)\\s*=\\s*(.*)\\s*(&|$)");
		Matcher Pars = getPars.matcher(phpParams);
		
		
		//find and store every parameter and its value
		while (Pars.find()){
			params.put(Pars.group(1).trim(),Pars.group(2).trim());
			
		}
		params.toString();
		
		//read File as string
		try (BufferedReader reader = new BufferedReader( new FileReader (fileToGet))){
			String         line = null;
	    	StringBuilder  stringBuilder = new StringBuilder();
	    	String         ls = System.getProperty("line.separator");

	    	while( ( line = reader.readLine() ) != null ) {
	    		stringBuilder.append( line );
	    		stringBuilder.append( ls );
	    	
	    	}
	    	phpCont = stringBuilder.toString();
	    }
		catch (FileNotFoundException ie){
			Status = "404 Not Found";
			GetError = true;
			throw ie;
		}catch (IOException e){
			Status = "500 Internall Server Error";
			GetError = true;
			throw e;
		}
	
		
		String before;
		String changed;
		int index = 0 ;
		int lastInd = 0;
		
		
		
		while (phpCont.substring(index, phpCont.length()).contains("<?php")){
			
		index = phpCont.indexOf("<?php",index) + 4;
		lastInd = phpCont.indexOf("?>",index);
		
		before = phpCont.substring(index, lastInd);
		changed = before;
		
		phpCont.substring(index,lastInd);
		//change parameters to their values
		for (Map.Entry<String, String> entry : params.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    // now replace them
		    if (changed.contains("['"+key+"']")){
	        	changed = changed.replace("['"+key+"']",value);
	        }
		}
		phpCont = phpCont.replace (before, changed);
	}	
		
		finalForm = phpCont.getBytes();
		
		return finalForm;
	}
	
	
	//prints a string representation of request info
	@Override
	public String toString(){
		
		
		StringBuilder printed = new StringBuilder();
		
		if (fileToGet != null){
			printed.append("File: ");
			printed.append(fileToGet);
			printed.append("\n");
		}
		if (Version != null){
			printed.append("Version: ");
			printed.append(Version);
			printed.append("\n");
			
		}
		if (Host != null){
			printed.append("Host: ");
			printed.append(Host);
			printed.append("\n");
			
		}
		
		if (Connection != null){
			printed.append("Connection: ");
			printed.append(Connection);
			printed.append("\n");
			
		}
		if (acceptLang!=null){
			printed.append("Language: ");
			for (String temp: acceptLang){
				
				printed.append(temp).append(" ");
				
			}
		}
		
		if (userAgent!=null){
			printed.append("User-Agent: ");
			printed.append(userAgent);
			printed.append("\n");
		}
		
		
		return (printed.toString());
	}
	
	
}
