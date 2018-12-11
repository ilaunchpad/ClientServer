import java.io.*; 
import java.text.*; 
import java.util.*;



import java.net.*; 
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
  
// Server class 
public class Server  
{ 
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5056); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            
            {
            	  //System.out.println("Trying to connect");
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
               
                ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());
                output.writeObject("Sending from server");
                ObjectInputStream input = new ObjectInputStream(s.getInputStream());
                System.out.println(input.readObject()); 
                System.out.println("Assigning new thread for this client"); 
                 
  
                // create a new thread object 
                Thread t = new ClientHandler(s, input, output);
                t.start();  
                
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  

