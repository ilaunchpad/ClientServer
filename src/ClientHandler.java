import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import Utilities.DHKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class ClientHandler extends Thread  
{ 
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
    //final DataInputStream dis; 
    //final DataOutputStream dos; 
    final ObjectInputStream input;
    final ObjectOutputStream output;
    final Socket s; 
    
    
      
  
    // Constructor 
    public ClientHandler(Socket s, ObjectInputStream input, ObjectOutputStream output)  
    { 
        this.s = s; 
        this.input = input; 
        this.output = output; 
    } 
  
    @Override
    public void run()  
    { 
    	  	  
        String received = null; 
        String toreturn; 
        byte[] ClientEncKey;
        byte[] ServerSecretKey;
        ArrayList  paramList = DHKey.AliceGenerateParameters();
        BigInteger G = (BigInteger) paramList.get(0);
   	    BigInteger P     = (BigInteger) paramList.get(1);
   	    int L     = (int) paramList.get(2);
   	    byte [] ServerEncKey = (byte[]) paramList.get(3);
   	    KeyAgreement ka = (KeyAgreement) paramList.get(4);
   	        
        while (true)  
        { 
            try {      	 
            	   output.writeObject(G);
               output.writeObject(P); 
               output.writeObject(L);
               output.writeObject(ServerEncKey);
               
               ClientEncKey = (byte[]) input.readObject();
               if (ClientEncKey !=null) {
           		 
   			    ServerSecretKey = DHKey.GenerateSecretKey(ClientEncKey, ka );
   				SecretKey ServerDeskey = DHKey.GenerateDesKey(ServerSecretKey) ;
   				System.out.println("alice Des Key " + ServerDeskey);
           
           }
             
            
            	 
            	 break;
               
            
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
          } 
          
         try
         { 
            // closing resources 
            //this.dis.close(); 
            //this.dos.close(); 
        	 this.output.close();
        	 this.input.close();
              
         }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 
