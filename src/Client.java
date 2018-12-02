
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import Utilities.DHKey; 
  
// Client class 
public class Client  
{ 
	static BigInteger G;
	static BigInteger P;
	static int        L;
	static byte []    serverEncKey ;
	 static byte [] clientEncPk;
    public static void main(String[] args) throws IOException  
    { 
        try
        
        { 
        	
        
            Scanner scn = new Scanner(System.in); 
              
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost"); 
      
            // establish the connection with server port 5056 
            Socket s = new Socket(ip, 5056); 
            System.out.println("Make socket connection");
            ObjectInputStream input = new ObjectInputStream(s.getInputStream());
            System.out.println((String) input.readObject() );
            ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());
            output.writeObject("I received your message");
            System.out.println("First handshake done");
            
            
            	//TimeUnit.SECONDS.sleep(300);
           
      
            // obtaining input and out streams 
            //DataInputStream dis = new DataInputStream(s.getInputStream()); 
            //DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
      
            // the following loop performs the exchange of 
            // information between client and client handler 
            
            
           // ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());
           
            while (true)  
            { 
                
            	     G  =(BigInteger) input.readObject();            
                 P = (BigInteger) input.readObject();
                 L = (int) input.readObject();  
                 
                 serverEncKey = (byte[]) input.readObject();
                 System.out.println("This is client side L " + serverEncKey );
                
                //Get Client Parameters
                if (serverEncKey !=null) {
                		ArrayList  ClientParamList =DHKey.BobGenerateParameters(P, G,L); 
                		byte []clientEncPK = (byte[]) ClientParamList.get(0);
            		    KeyAgreement bobka = (KeyAgreement) ClientParamList.get(1);   
            		    output.writeObject(clientEncPk);
        				byte[] ClientSecretKey = DHKey.GenerateSecretKey(serverEncKey, bobka );
        				SecretKey bobDeskey = DHKey.GenerateDesKey(ClientSecretKey) ;
        				System.out.println("alice Des Key " + bobDeskey);
                
                }
                  				
                System.exit(0);
                String tosend = scn.nextLine(); 
                
                //dos.writeUTF(tosend); 
                 
                  
                  
                // If client sends exit,close this connection  
                // and then break from the while loop 
                if(G.equals("Exit")) 
                { 
                    System.out.println("Closing this connection : " + s); 
                    s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                } 
                  
                // printing date or time as requested by client 
               // String received = dis.readUTF(); 
                //System.out.println(received); 
            } 
              
            // closing resources 
            scn.close(); 
            output.close(); 
            input.close(); 
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 
} 