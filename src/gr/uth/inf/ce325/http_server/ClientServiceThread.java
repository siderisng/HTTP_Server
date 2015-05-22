
package gr.uth.inf.ce325.http_server;
import java.net.*;
import java.io.*;

public class ClientServiceThread extends Thread{
	private String ServerName;
	private Socket myClientSocket;
	private boolean m_bRunThread = true; 
	private boolean serverON;
	private String rootDir;
	private logFiles lg;
	private boolean runphp;
	private Statistics myStats;
	
	//--------Constructors
	public ClientServiceThread() 
	{ 
		
		super(); 
		serverON = true;
		
	} 
	
	ClientServiceThread(Socket s, String sN, String rd,logFiles l,boolean r,Statistics STS) 
	{ 
		myClientSocket = s; 
		serverON = true;
		ServerName = sN;
		rootDir = rd;
		lg = l;
		runphp=r;
		myStats = STS;
	} 
	
	
	//Executes Client's thread
	@Override
	public void run () 
	{            
		BufferedReader in = null; //Reads data from socket 
		PrintWriter out = null; //Used for sending data
		long startT = System.currentTimeMillis();
		
		// Print out details of this connection 
		System.out.println("Accepted Client Address - " + myClientSocket.getInetAddress().getHostName()); 
		
		try
		{   
			//init in and out
			in = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream())); 
			out = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream())); 
			 
			
			//Used for sending the file 
			OutputStream fWrite;
			
			// At this point, we can read for input and reply with appropriate output. 
			GET_Request GR= new GET_Request(ServerName ,rootDir, runphp); //New request Object
			
			// Run in a loop until m_bRunThread is set to false 
			while(m_bRunThread) 
			{                    
				// read incoming stream 
				String clientCommand = in.readLine(); //Read next command 
				System.out.println("Client From Ip : "+ myClientSocket.getInetAddress().toString() + " Says :" + clientCommand); 
				
				//Parse command 
				if (clientCommand != null){
					GR.buildRequest(clientCommand);
				}else{ m_bRunThread = false; }
				if(!serverON) 
				{ 
					// Special command. Quit this thread 
					System.out.print("Server has already stopped"); 
					out.println("Server has already stopped"); 
					out.flush(); 
					m_bRunThread = false;   
				}else {
					//if server has all the data needed
					if (GR.isFinished()||GR.existsError()){
						
						fWrite = myClientSocket.getOutputStream();				//send Server's response
						
						if (!GR.existsError()){                            //try Sending the file
							try {
                                
								byte [] resp = GR.getResponse().getBytes();  
								byte [] onse;
								
								if (GR.isPHPar()){
									onse = GR.parsePHP();
									
									
								}else{
									onse = GR.sendFile();
								}
								byte [] response = new byte[resp.length + onse.length];
								System.arraycopy(resp, 0, response, 0, resp.length);
								System.arraycopy(onse, 0, response, resp.length, onse.length);
								
								fWrite = myClientSocket.getOutputStream();
								fWrite.write(response,0,response.length);
								fWrite.flush();
								
							}catch(IOException ie){
								//if something went wrong
								System.out.println(ie.getCause());
								GR.setStatus("Error 500: Internal Server Error");
							}
						}
						if (GR.existsError()){
							byte [] errorResp;
							String errorResponse;
							errorResponse = GR.getResponse() + "<html><head><title>" + GR.getStatus() + "</title></head><body><h1>" + GR.getStatus() + "</h1></body></html>";
							errorResp = errorResponse.getBytes("UTF-8");
							fWrite = myClientSocket.getOutputStream();
							fWrite.write(errorResp,0,errorResp.length);
							fWrite.flush();
							
						}
						
						//keep log of this entry for the statistics
						//find serve time (to use for finding out average serve time)
						long servedTime = startT - System.currentTimeMillis()/1000;
						myStats.ServeTime(servedTime);
						//increment the right variable in statistics based on response status
						if (GR.getStatus().contains("200")){
							myStats.incrSuccessR();
						}else if (GR.getStatus().contains("400")){
							myStats.incrBadReq();
						}else if (GR.getStatus().contains("404")){
							myStats.incrNotFound();
						}else if (GR.getStatus().contains("500")){
							myStats.incrIntError();
						}else if (GR.getStatus().contains("405")){
							myStats.incrMethNotAll();
						}
						//keep log and write info to access and error files
						
						try{
							lg.keepLOG(myClientSocket.getInetAddress() , GR);
						}
						catch (IOException e){
							
							System.out.println(e.getCause());
						}
						
						
						m_bRunThread = false;//End connection
					}
				}
			} 
		} 
		catch(Exception e) 
		{ 
			e.printStackTrace(); 
		} 
		finally
		{ 
			// Clean up 
			try
			{                    
				in.close(); 
				out.close(); 
				myClientSocket.close(); 
				System.out.println("...Stopped"); 
			} 
			catch(IOException ioe) 
			{ 
				ioe.printStackTrace(); 
			} 
		} 
	} 
	
	
} 
