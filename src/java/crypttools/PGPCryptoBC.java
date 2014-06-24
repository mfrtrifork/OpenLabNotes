package crypttools;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
 
/**
 * 
 * Copyright George El-Haddad</br>
 * <b>Time stamp:</b> Dec 6, 2012 - 11:41:43 AM<br/>
 * @author George El-Haddad
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


/**
 * 
 * @author franz
 *	Edited for use in OpenLabFramework and store keys on domain class instead of files
 */
public class PGPCryptoBC {
	
	PGPKeyRingGenerator pgpKeyRingGen = null;
	
	byte[] armoredSecretKey = null;
	byte[] armoredPublicKey = null;
	
	public void generateKeys(String username, String passphrase){
        try {
            BigInteger primeModulous = PGPTools.getSafePrimeModulus(PGPTools.PRIME_MODULUS_4096_BIT);
            BigInteger baseGenerator = PGPTools.getBaseGenerator();
            ElGamalParameterSpec paramSpecs = new ElGamalParameterSpec(primeModulous, baseGenerator);
             
            KeyPair dsaKeyPair = PGPTools.generateDsaKeyPair(1024);
            KeyPair elGamalKeyPair = PGPTools.generateElGamalKeyPair(paramSpecs);
             
            this.pgpKeyRingGen = PGPTools.createPGPKeyRingGenerator(
                    dsaKeyPair,
                    elGamalKeyPair,
                    username,
                    passphrase.toCharArray()
            );
            PGPSecretKeyRing pgpSecKeyRing = this.pgpKeyRingGen.generateSecretKeyRing();
            PGPPublicKeyRing pgpPubKeyRing = this.pgpKeyRingGen.generatePublicKeyRing();
            
            /* Save secret key*/
            ByteArrayOutputStream pgpSecKeyRingOutputStream = new ByteArrayOutputStream();
            ArmoredOutputStream aosSecret = new ArmoredOutputStream(pgpSecKeyRingOutputStream);
            pgpSecKeyRing.encode(aosSecret);
            aosSecret.close();
            this.armoredSecretKey = pgpSecKeyRingOutputStream.toByteArray();
            
            /* Save public key*/
            ByteArrayOutputStream pgpPubKeyRingOutputStream = new ByteArrayOutputStream();
            ArmoredOutputStream aosPublic = new ArmoredOutputStream(pgpPubKeyRingOutputStream);
            pgpPubKeyRing.encode(aosPublic);
            aosPublic.close();
            this.armoredPublicKey = pgpPubKeyRingOutputStream.toByteArray();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
	}
	
	public String signData(String data, String passphrase) throws Exception{
		Security.addProvider(new BouncyCastleProvider());
		InputStream keyInputStream = new ByteArrayInputStream(this.armoredSecretKey);
		PGPSecretKey pgpSecretKey = readSecretKey(keyInputStream);
		PGPPrivateKey pgpPrivateKey = pgpSecretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(passphrase.toCharArray()));
		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSecretKey.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, pgpPrivateKey);
 		
        @SuppressWarnings("unchecked")
        Iterator<String> it = pgpSecretKey.getPublicKey().getUserIDs();
        if (it.hasNext()) {
            PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();
            spGen.setSignerUserID(false, it.next());
            signatureGenerator.setHashedSubpackets(spGen.generate());
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        OutputStream outputStream = new ArmoredOutputStream(byteOutputStream);
        PGPCompressedDataGenerator compressDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZLIB);
        BCPGOutputStream bcOutputStream = new BCPGOutputStream(compressDataGenerator.open(outputStream));
        signatureGenerator.generateOnePassVersion(false).encode(bcOutputStream);        
        
        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        File fileToSign = File.createTempFile("temp",".scrap");
        FileUtils.writeStringToFile(fileToSign, data);
        
        OutputStream literalDataGenOutputStream = literalDataGenerator.open(bcOutputStream, PGPLiteralData.BINARY, fileToSign);
        FileInputStream fis = new FileInputStream(fileToSign);
        int ch;
        while ((ch = fis.read()) >= 0) {
            literalDataGenOutputStream.write(ch);
            signatureGenerator.update((byte)ch);
        }
        
        literalDataGenerator.close();
        fis.close();
 
        signatureGenerator.generate().encode(bcOutputStream);
        compressDataGenerator.close();
        outputStream.close();
        
        fileToSign.delete();
        return new String(byteOutputStream.toByteArray(), "UTF-8");
	}
	
	public boolean validateData(String data, String publicKey) throws Exception{
		Security.addProvider(new BouncyCastleProvider());
		File fileToVerify = File.createTempFile("temp",".privateScrap");
        FileUtils.writeStringToFile(fileToVerify, data);
        
        File publicKeyFile = File.createTempFile("temp",".publicScrap");
        // Creates an exception
//        System.out.println(this.armoredPublicKey);
//        String armoredKeyString = getPublicKey();
//        System.out.println(armoredKeyString);
        FileUtils.writeStringToFile(publicKeyFile, publicKey);
        //FileUtils.writeStringToFile(publicKeyFile, new String(this.armoredPublicKey, "UTF-8"));
        
        try {
        	InputStream in = PGPUtil.getDecoderStream(new FileInputStream(fileToVerify));
            
            PGPObjectFactory pgpObjFactory = new PGPObjectFactory(in);
            PGPCompressedData compressedData = (PGPCompressedData)pgpObjFactory.nextObject();
             
            //Get the signature from the file
              
            pgpObjFactory = new PGPObjectFactory(compressedData.getDataStream());
            PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList)pgpObjFactory.nextObject();
            PGPOnePassSignature onePassSignature = onePassSignatureList.get(0);
             
            //Get the literal data from the file
     
            PGPLiteralData pgpLiteralData = (PGPLiteralData)pgpObjFactory.nextObject();
            InputStream literalDataStream = pgpLiteralData.getInputStream();
             
            InputStream keyIn = new FileInputStream(publicKeyFile);
            PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn));
            PGPPublicKey key = pgpRing.getPublicKey(onePassSignature.getKeyID());
            
            FileOutputStream literalDataOutputStream = new FileOutputStream(pgpLiteralData.getFileName());
            onePassSignature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), key);
     
            int ch;
            while ((ch = literalDataStream.read()) >= 0) {
                onePassSignature.update((byte)ch);
                literalDataOutputStream.write(ch);
            }
     
            literalDataOutputStream.close();
             
            //Get the signature from the written out file
     
            PGPSignatureList p3 = (PGPSignatureList)pgpObjFactory.nextObject();
            PGPSignature signature = p3.get(0);
             
            //Verify the two signatures
            boolean valid = onePassSignature.verify(signature);
            return valid;
        } catch (Exception e) {
        	System.out.println("Got an Exception: " + e.getMessage());
        	return false;
            //do something clever with the exception
        } finally {
            fileToVerify.delete();
            publicKeyFile.delete();
        }
	}
	
	public String getSecretKey() throws UnsupportedEncodingException{
		return new String(this.armoredSecretKey, "UTF-8");
	}
	public String getPublicKey() throws UnsupportedEncodingException{
		return new String(this.armoredPublicKey, "UTF-8");
	}
	public void setSecretKey(String secretKey){
		this.armoredSecretKey = secretKey.getBytes();
	}
	public void setPublicKey(String publicKey){
		this.armoredPublicKey = publicKey.getBytes();
	}


	   /**
     * <p>Return the first suitable key for encryption in the key ring
     * collection. For this case we only expect there to be one key
     * available for signing.</p>
     * 
     * @param input - the input stream of the key PGP Key Ring
     * @return the first suitable PGP Secret Key found for signing
     * @throws IOException
     * @throws PGPException
     */
    @SuppressWarnings("unchecked")
    private static PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException
    {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input));
        Iterator<PGPSecretKeyRing> iter = pgpSec.getKeyRings();
        PGPSecretKey secKey = null;
         
        while (iter.hasNext() && secKey == null) {
            PGPSecretKeyRing keyRing = iter.next();
            Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
             
            while (keyIter.hasNext()) {
                PGPSecretKey key = keyIter.next();
                if (key.isSigningKey()) {
                    secKey = key;
                    break;
                }
            }
        }
         
        if(secKey != null) {
            return secKey;
        }
        else {
            throw new IllegalArgumentException("Can't find signing key in key ring.");
        }
    }
}
