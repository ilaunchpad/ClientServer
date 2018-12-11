
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
	
	 //static byte [] clientEncPk;
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
            
           
            while (true)  
            { 
                 	SecretKey bobDeskey = null;
            	     G  =(BigInteger) input.readObject();            
                 P = (BigInteger) input.readObject();
                 L = (int) input.readObject();  
                 
                 serverEncKey = (byte[]) input.readObject();
                 System.out.println("This is client side L " + serverEncKey );
                
                //Get Client Parameters
                if (serverEncKey !=null) {
                		ArrayList  ClientParamList =DHKey.BobGenerateParameters(P, G,L); 
                		byte []clientEncPK = (byte[]) ClientParamList.get(0);
                		System.out.println("ClientEncPK" + clientEncPK);
            		    KeyAgreement bobka = (KeyAgreement) ClientParamList.get(1);   
            		    output.writeObject(clientEncPK);
        				byte[] ClientSecretKey = DHKey.GenerateSecretKey(serverEncKey, bobka );
        				 bobDeskey = DHKey.GenerateDesKey(ClientSecretKey) ;
        				System.out.println("Client Des Key " + bobDeskey);
        				System.out.println("The key exchange is done. And the communication is secured");
                
                }
                 
                byte[] ciphertext = (byte[]) input.readObject();
                String message = DHKey.decryptcipher(bobDeskey, ciphertext);
                System.out.println("This is received message");
                System.out.println(message);
                
              
                if(message.equals("Exit")) 
                { 
                    System.out.println("Closing this connection : " + s); 
                    s.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                } 
                  
                
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