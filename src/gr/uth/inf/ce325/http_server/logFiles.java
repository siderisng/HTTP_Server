package gr.uth.inf.ce325.http_server;
//contains all the basic operations in basic IO Files useful for
//the server

import java.io.*;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.*;



public class logFiles {
	private String accessFile; //File to keep log
	private String errorFile;	//File to keep errors etc
	
	//constructors------------
	public logFiles(String accessFile,String errorFile){
		this.accessFile = accessFile;
		this.errorFile = errorFile;
	}
	
	public logFiles(){}
	
	
	//Set Methods-----------------
	//Sets access filepath
	public void setAccess(String af){
		
		accessFile = af;
	}
	//Sets error filepath
	public void setError(String ef){
		
		errorFile = ef;
	}
	
	public String getAccessF(){
		
		return accessFile;
	}
	
	public String getErrorF(){
		
		return errorFile;
		
	}
	
	
	
	//Keeps Log whenever necessary
	public void keepLOG(InetAddress ip, GET_Request GR) throws IOException{
		
		StringBuilder toWrF = new StringBuilder();//for access
		StringBuilder toErF = new StringBuilder();//for error
		//Get current Date
		Date this_time = new Date();
		Locale localeEN = Locale.ENGLISH;
		DateFormat dateEN = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,localeEN);
		
		
		//Write to access file
		toWrF.append(ip).append(" - ").append("[").append(dateEN.format(this_time)).append("]  GET  ");
		toWrF.append(GR.filerequest()).append("  HTTP/").append(GR.getVersion()).append(" -> ").append(GR.getStatus());
		toWrF.append("  User-Agent: ").append(GR.getAgent()).append("\n");
		
		File afile = new File(accessFile);
		
		File efile = new File(errorFile);
		
		if (!afile.exists())
		{
			if (!afile.getParentFile().exists()){
				afile.createNewFile();
			}
		}
		if (!efile.exists())
		{
			if (!efile.getParentFile().exists()){
				efile.createNewFile();
			}
		}
		
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(accessFile, true)))) {
			out.println(toWrF.toString());
		}catch (IOException e) {
			throw e;//Don't forget to catch it
		}
		
		//iif there were errors write to error file
		if (!GR.getStatus().contains("200")) {
			
			toErF.append(ip).append(" - ").append("[").append(dateEN.format(this_time)).append("]  GET  ");
			toErF.append(GR.filerequest()).append("  HTTP/").append(GR.getVersion()).append(" -> ").append(GR.getStatus()).append("\n");
			//find what is exception s stack trace....   
			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(errorFile, true)))) {
				out.println(toErF.toString());
			}catch (IOException e) {
				throw e;//handle it in client thread
			}
			
		}
		
	}
	
	
	//not used function
	//finds the last time the file was seen 
	public String findLastUsedDate(String FileName) throws FileNotFoundException{
		String dateFromFile = "Not yet Downloaded";
		File toSearch= new File(accessFile);
		String line;
		
		try (Scanner scanner = new Scanner(toSearch)){
			
			
			while (scanner.hasNextLine()){
				line = scanner.nextLine();
				if (line.contains(FileName)){
					
					line = line.substring(line.indexOf("[") + 1);
					dateFromFile = line.substring(0, line.indexOf("]")); //keeps the last line only
					
				}
				
			}
			
		}catch(FileNotFoundException ie){
			throw ie; //don t forget to catch it
			
		}
		
		return (dateFromFile);
	}
	
	
	//Print methods------------------
	
	//Prints representation of file
	@Override
	public String toString(){
		StringBuilder printed = new StringBuilder();
		
		
		printed.append ("\nLog Access File Path: ");
		printed.append (accessFile);
		printed.append ("\nLog Error File Path: ");
		printed.append (errorFile);
		return (printed.toString());
		
		
	}
}
