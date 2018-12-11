import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import Utilities.DHKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class ClientHandler extends Thread  
{ 
    private static Object ciphertext;
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
    
    public static byte[] generateciphertext(SecretKey key) {
    	String message = "Exit";
    	byte[] ciphertext = null;
    	try {
			Cipher c = Cipher.getInstance("DES/ECB/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, key);
			 ciphertext = c.doFinal(message.getBytes());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ciphertext;
    }
  
    @Override
    public void run()  
    { 
    	  	  
        String received = null; 
        String toreturn; 
        byte[] ClientEncKey;
        byte[] ServerSecretKey;
        SecretKey ServerDeskey = null;
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
               System.out.println("This is clientenckey" + ClientEncKey);
               if (ClientEncKey !=null) {
           		 
   			    ServerSecretKey = DHKey.GenerateSecretKey(ClientEncKey, ka );
   				 ServerDeskey = DHKey.GenerateDesKey(ServerSecretKey) ;
   				System.out.println("Server Des Key " + ServerDeskey);
   				
   		    }
               
             byte[] tosend =  DHKey.generateciphertext(ServerDeskey, "Exit") ;  
             output.writeObject(tosend);
             System.out.print("Message sent");
             
             break;
            	        
            
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
          } 
          
         try
         { 
           
        	 this.output.close();
        	 this.input.close();
              
         }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 
