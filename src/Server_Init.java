import gr.uth.inf.ce325.http_server.*;
import java.io.IOException;

public class Server_Init{




    public static void main(String[] args) throws IOException{
       
    
        serverCreator servC = new serverCreator();
        Server myServer;
        
        myServer = servC.createServer("test.txt");
        try{myServer.Serving();}
        catch (Exception ex){
            System.out.println(ex.getCause());
        }
        
 }
  
    
}