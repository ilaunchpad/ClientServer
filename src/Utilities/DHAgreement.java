package Utilities;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
//import com.sun.crypto.provider.SunJCE;
import java.util.Base64;
import java.math.BigInteger;
import java.io.OutputStream;
import java.io.InputStream;

public class DHAgreement implements Runnable{
	byte bob[], alice[];
	boolean doneAlice = false; 
	byte[] ciphertext;

	BigInteger aliceP;
	BigInteger aliceG;
	int aliceL;

	public synchronized void run() {
		if (!doneAlice) {
			doneAlice = true;
			doAlice();
		}
		else doBob();
	}

	public synchronized void doAlice() {
		try {
			// Step 1:  Alice generates a key pair
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
			kpg.initialize(1024);
			KeyPair kp = kpg.generateKeyPair();
			
			// Step 2:  Alice sends the public key and the
			// 		Diffie-Hellman key parameters to Bob
			Class dhClass = Class.forName(
								"javax.crypto.spec.DHParameterSpec");
			DHParameterSpec dhSpec = (
							(DHPublicKey) kp.getPublic()).getParams();
			aliceG = dhSpec.getG();
			aliceP = dhSpec.getP();
			aliceL = dhSpec.getL();
	 
			alice = kp.getPublic().getEncoded();
			notify();

			// Step 4 part 1:  Alice performs the first phase of the
			//		protocol with her private key
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(kp.getPrivate());

			// Step 4 part 2:  Alice performs the second phase of the
			//		protocol with Bob's public key
			while (bob == null) {
				wait();
			}
			KeyFactory kf = KeyFactory.getInstance("DH");
			X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(bob);
			PublicKey pk = kf.generatePublic(x509Spec);
			ka.doPhase(pk, true);

			// Step 4 part 3:  Alice can generate the secret key
			byte secret[] = ka.generateSecret();

			// Step 6:  Alice generates a DES key
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			DESKeySpec desSpec = new DESKeySpec(secret);
			SecretKey key = skf.generateSecret(desSpec);
			
			// Step 7:  Alice encrypts data with the key and sends
			//		the encrypted data to Bob
			Cipher c = Cipher.getInstance("DES/ECB/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, key);
			ciphertext = c.doFinal(
						"Stand and unfold yourself".getBytes());
			notify();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void doBob() {
		try {
			// Step 3:  Bob uses the parameters supplied by Alice
			//		to generate a key pair and sends the public key
			while (alice == null) {
				wait();
			}
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
			DHParameterSpec dhSpec = new DHParameterSpec(
								aliceP, aliceG, aliceL);
			kpg.initialize(dhSpec);
			KeyPair kp = kpg.generateKeyPair();
			bob = kp.getPublic().getEncoded();
			notify();

			// Step 5 part 1:  Bob uses his private key to perform the
			//		first phase of the protocol
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(kp.getPrivate());

			// Step 5 part 2:  Bob uses Alice's public key to perform
			//		the second phase of the protocol.
			KeyFactory kf = KeyFactory.getInstance("DH");
			X509EncodedKeySpec x509Spec =
							new X509EncodedKeySpec(alice);
			PublicKey pk = kf.generatePublic(x509Spec);
			ka.doPhase(pk, true);
		

			// Step 5 part 3:  Bob generates the secret key
			byte secret[] = ka.generateSecret();

			// Step 6:  Bob generates a DES key
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			DESKeySpec desSpec = new DESKeySpec(secret);
			SecretKey key = skf.generateSecret(desSpec);
			
			// Step 8:  Bob receives the encrypted text and decrypts it
			Cipher c = Cipher.getInstance("DES/ECB/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, key);
			while (ciphertext == null) {
				wait();
			}
			byte plaintext[] = c.doFinal(ciphertext);
			System.out.println("Bob got the string " +
						new String(plaintext));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		DHAgreement test = new DHAgreement();
		new Thread(test).start();		// Starts Alice
		new Thread(test).start();		// Starts Bob
	}
}
