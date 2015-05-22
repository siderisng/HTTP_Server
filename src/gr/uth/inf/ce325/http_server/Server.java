//George Sideris - sideris@uth.gr
//Fotis Tsokos - tsokos@uth.gr

package gr.uth.inf.ce325.http_server;
import java.net.*;
import java.io.*;
import java.util.*;


public class Server {
	
	private Statistics myStats; //initialized here this class helps us keep track of statistic
	private int listenPort; //for basic server connection
	private int statsPort;    //for stats server connection
	private logFiles LOG;     //basic logs like access and error files
	private String rootDir;   //server's root directory 
	private boolean runPhp;   //true if it does run php false otherwise
	private static List <String> denied; //list of blocked ips
	private boolean ServerIsOn ; //a useless boolean as it seems. helps for the endless loop
	private ServerSocket servSock;	//the server socket 
	
	private final static String ServerName = "CE321-Server";
	///////////constructors/////////
	public Server(){
		
		runPhp = false;
		ServerIsOn = false;
	}
	
	public Server(int lP, int sP, logFiles log, String rD, boolean rP, List <String> d){
		
		listenPort = lP;
		statsPort = sP;    
		LOG = log;
		rootDir = rD;
		runPhp = rP;
		denied = d;
		ServerIsOn = false;
		
	}
	
	
	//////methods/////////////////////
	
	//set methods, used for variable initialization 
	//---------
	//set basic server listening port
	public void setListeningPort(int lP){
		
		listenPort = lP;
		
	}
	
	//set the statistic port
	public void setStatisticsPort(int sP){
		
		statsPort = sP;
		
	}
	
	//sets log files
	public void setLOG(logFiles log){
		
		LOG = log;
		
	}
	
	//sets root dir
	public void setRootDirectory(String rD){
		
		rootDir = rD;
		
	}
	
	//sets the list of denied ips
	public void setDeniedList(List <String> d){
		
		denied = d;
		
	}
	
	//set runs php to true
	public void runsPHP(){
		
		runPhp = true;
		
	}
	
	//returns the root path
	public String getRoot(){
		
		return rootDir;
		
	}
	
	
	public  boolean serverStillRunning(){return ServerIsOn;}
	public  void serverNeedsToStop(){ServerIsOn = false;}        
	
	//source: http://www.mysamplecode.com/2011/12/java-multithreaded-socket-server.html
	//Server init and endless loop to serve all of our beloved clients
	public void Serving() throws Exception {
		
		

		//create Server Socket
		while (!ServerIsOn){
		try
		{ 
			servSock = new ServerSocket(listenPort);
			System.out.println("Server: "+ServerName+" is on"); 
			ServerIsOn = true;
		} 
		catch(Exception ioe) 
		{ 
			System.out.println ("Couldn't create Server : " + ioe.getCause());
			System.out.print ("Trying to change port from " +listenPort +" to " );
			listenPort++;
			System.out.println (listenPort);
		}
		}
		//Start Statistic Server
		myStats = new Statistics(ServerName);
		StatisticsServerThread statServ= new StatisticsServerThread(statsPort,myStats);
		statServ.start(); 
		
		
		//Start serving
		while(ServerIsOn){ 
			
			try
			{ 
				// Accept incoming connections. 
				Socket clientSocket = servSock.accept(); 
				//Check if client is in the black list
				if (isDenied(clientSocket.getInetAddress())){
					
					throw new Exception("IP :" +clientSocket.getRemoteSocketAddress().toString()+" tried to use Server");
				}
				
				//Start Client handling
				ClientServiceThread cliThread = new ClientServiceThread(clientSocket, ServerName , rootDir, LOG, runPhp,myStats);
				cliThread.start(); 
				
			} 
			catch(IOException ioe) 
			{ 
				System.out.println("Exception encountered on accept. Ignoring. Stack Trace :"); 
				ioe.printStackTrace(); 
			}
			catch (Exception e){
				System.out.println(e.getMessage());
			}
			
		}
		//Close socket when done
		try
		{ 
			servSock.close(); 
			System.out.println("Server Stopped"); 
		} 
		catch(Exception ioe) 
		{ 
			System.out.println("Problem stopping server socket"); 
			System.exit(-1); 
		} 
		
		
	} 
	
	
	//Check if client IP belongs to the black list 
	protected static boolean isDenied(InetAddress IPToTest) throws UnknownHostException{
		String IPTo = IPToTest.toString().substring(1);
		byte[] toTest = IPToTest.getAddress();
		boolean flag=true;
		
		//for each blocked ip
		for (String IPD: denied){
			
			//if not a mask
			if (!IPD.contains("/")){ 
				//make a check
				if (IPD.equals(IPTo)){return true;}
			}
			//else find mask and a byte representation of it
			else{
				int maskL = Integer.parseInt(IPD.substring(IPD.lastIndexOf("/") + 1));
				byte[] ipD = InetAddress.getByName(IPD.substring(0, IPD.indexOf('/'))).getAddress();
				String maskS = "";
				
				//for all four mask fields turn value to the appropriate power of 2
				for (int i = 0; i < 4; i++ ){
					
					if (maskL < 8){
						if (maskL == 0){
							maskS = maskS + ".0";
						}else{
							
							int powerT = 256 - (int)Math.pow(2, 8 - maskL);
							
							maskS = maskS + "." + powerT;
							
							maskL = 0;
						}
					}else{
						maskL -= 8; 
						maskS = maskS + ".255";
					}
				}
				//get mask and check if ip address belongs there            
				byte[] mask = InetAddress.getByName(maskS.substring(1).trim()).getAddress(); 
				flag = false;
				for (int j = 0; j < mask.length ; j ++ ){
					if ((toTest[j] & mask[j])!=(ipD[j] & mask[j])) { flag = true; }
				}
				if (!flag){return true;}
			}
		}
		//if ip doesn't belong in the list let her be
		return false;
	}
	
	
	
	
	//Basic Server Representation
	@Override
	public String toString(){
		StringBuilder printed = new StringBuilder();
		
		
		printed.append ("Listening Port: ");
		printed.append(listenPort);
		printed.append ("\nStatistics Port: ");
		printed.append (statsPort);
		printed.append (LOG.toString());
		printed.append ("\nRoot Directory: ");
		printed.append (rootDir);
		printed.append ("\nRuns PHP: ");
		printed.append (runPhp);
		printed.append ("\nBlocked IPs: ");
		
		if (denied!=null){
			for (String temp: denied){
				
				printed.append(temp).append(" ");
				
			}
		}
		printed.append("\n");
		
		return (printed.toString());
	}   
	
	
	
}
