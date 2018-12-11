package Utilities;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.math.BigInteger;
public class DHKey {
	//static byte bob[];
	//static byte alice[];
	byte[] ciphertext;
	//static BigInteger aliceP;
	//static BigInteger aliceG;
	//tatic int aliceL;
	
	
	public  static final ArrayList<Object> AliceGenerateParameters()  {
		BigInteger aliceP; 
		BigInteger aliceG;
		int aliceL;
		byte alice[];
		ArrayList<Object> param = new ArrayList<Object>();
      
		
		try {
			KeyPairGenerator kpg;
			KeyAgreement ka = null;
			kpg = KeyPairGenerator.getInstance("DH");
			kpg.initialize(1024);
			KeyPair kp = kpg.generateKeyPair();
			
			DHParameterSpec dhSpec = ((DHPublicKey) kp.getPublic()).getParams();
			aliceG = dhSpec.getG();
			aliceP = dhSpec.getP();
			aliceL = dhSpec.getL();
			alice = kp.getPublic().getEncoded();
			param.add(aliceG);
			param.add(aliceP);
			param.add(aliceL);
			param.add(alice);
		
			
		    ka = KeyAgreement.getInstance("DH");	
			ka.init(kp.getPrivate());
			param.add(ka);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return param;
	}
	
	public static final ArrayList<Object> BobGenerateParameters(BigInteger aliceP, BigInteger aliceG, int aliceL){
		byte bob[];
		ArrayList<Object> bobParam = new ArrayList<Object>();
		KeyPairGenerator kpg;
		
		try {
			
			kpg = KeyPairGenerator.getInstance("DH");
			kpg.initialize(1024);
			KeyPair kp = kpg.generateKeyPair();
			DHParameterSpec dhSpec = new DHParameterSpec(
					aliceP, aliceG, aliceL);
			kpg.initialize(dhSpec);
			bob = kp.getPublic().getEncoded();
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(kp.getPrivate());
			
			bobParam.add(bob);
			bobParam.add(ka);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bobParam;
	}
	
	public static byte[] GenerateSecretKey(byte[]encPK, KeyAgreement ka )  {
		KeyFactory kf;
		byte secret[] = null;
		try {
			kf = KeyFactory.getInstance("DH");
			X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(encPK);
			PublicKey pk = kf.generatePublic(x509Spec);
			ka.doPhase(pk, true);
			secret = ka.generateSecret();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return secret;
		
	}
	
	public static SecretKey GenerateDesKey(byte [] secret) {
		SecretKeyFactory skf;
		SecretKey key = null;
		try {
			skf = SecretKeyFactory.getInstance("DES");
			DESKeySpec desSpec = new DESKeySpec(secret);
			key = skf.generateSecret(desSpec);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return key;
	}
	
	 public static byte[] generateciphertext(SecretKey key, String message) {
	    //	String message = "Exit";
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
	 
	public static String decryptcipher(SecretKey key, byte[]ciphertext) {
		
		Cipher c;
		String s = null;
		try {
			c = Cipher.getInstance("DES/ECB/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, key);
			byte plaintext[] = c.doFinal(ciphertext);
			 String m = new String(plaintext);
			 if (m != null) {
				 s = m;
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		
		return s;
	}
	
	public static void main(String[] args) {
		
		ArrayList<Object> aliceparams=AliceGenerateParameters();
		BigInteger G = (BigInteger) aliceparams.get(0);
		BigInteger P = (BigInteger) aliceparams.get(1);	
		int L        = (int) aliceparams.get(2);
		ArrayList<Object> bobparams=BobGenerateParameters(P, G, L);
		
		byte []aliceEncPK = (byte[]) aliceparams.get(3);
		byte []bobEncPK = (byte[]) bobparams.get(0);
		
		KeyAgreement aliceka = (KeyAgreement) aliceparams.get(4);
		KeyAgreement bobka = (KeyAgreement) bobparams.get(1);
		byte[] aliceSecretKey = GenerateSecretKey(bobEncPK, aliceka );
		SecretKey aliceDeskey = GenerateDesKey(aliceSecretKey) ;
		System.out.println("alice Des Key " + aliceDeskey);
		//ArrayList<Object> bob = BobGenerateParameters();
		//System.out.println(second.get(4));
		//System.out.println(bob.get(1));
		System.out.println("Break");
		byte[] bobSecretKey = GenerateSecretKey(aliceEncPK, bobka );
		SecretKey bobDeskey = GenerateDesKey(bobSecretKey) ;
		System.out.println("alice Des Key " + bobDeskey);
		
		
		
		
	}
}
