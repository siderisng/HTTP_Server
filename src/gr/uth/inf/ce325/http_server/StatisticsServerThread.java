package gr.uth.inf.ce325.http_server;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;



public class StatisticsServerThread extends Thread{
	private int port;  //port for server connection
	private Statistics myStats; //class that includes fields to be send
	private boolean ServerIsOn; //boolean for endless loop
	
	//Constructor
	public StatisticsServerThread(int p,Statistics S){
		super(); 
		port = p;
		myStats = S;
		ServerIsOn = false;
	}
	
	//runs thread
	@Override
	public void run(){
		ServerSocket servSock = null;
		
		//create server socket for statistics server	
		
		while (!ServerIsOn){
		
			try
		
			{ 
			
				servSock = new ServerSocket(port); 
				System.out.println("Stats Server is On");
				ServerIsOn = true;
			} 
			catch(Exception ioe) 
			{	 
				System.out.println ("Couldn't create Statistics Server : " + ioe.getCause());
				System.out.print ("Trying to change port from " +port +" to " );
				port++;
				System.out.println (port);
			}
		
		}
		
		while(ServerIsOn){ 

			
			try
			{ 
				// Accept incoming connections. 
				Socket clientSocket = servSock.accept(); 
				//deny if blocked
				if (Server.isDenied(clientSocket.getInetAddress())){
					
					throw new Exception("IP :" +clientSocket.getRemoteSocketAddress().toString()+" tried to use Server");
				}
				//print stats to valid IPs
				PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream())); 
				
				out.println (myStats.toString());
				
				out.close();
				clientSocket.close();
			} 
			catch(IOException ioe) 
			{ 
				System.out.println("Exception encountered on accept. Ignoring. Stack Trace :"); 
				ioe.printStackTrace(); 
			}
			catch (Exception e){
				
				System.out.println (e.getMessage());
				
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
	
	
}
