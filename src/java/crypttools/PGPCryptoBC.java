package crypttools;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
 
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
public class PGPCryptoBC {
 
    public PGPCryptoBC() {
        try {
            String keysDir = System.getProperty("user.dir")+File.separator+"mykeys";
             
            BigInteger primeModulous = PGPTools.getSafePrimeModulus(PGPTools.PRIME_MODULUS_4096_BIT);
            BigInteger baseGenerator = PGPTools.getBaseGenerator();
            ElGamalParameterSpec paramSpecs = new ElGamalParameterSpec(primeModulous, baseGenerator);
             
            KeyPair dsaKeyPair = PGPTools.generateDsaKeyPair(1024);
            KeyPair elGamalKeyPair = PGPTools.generateElGamalKeyPair(paramSpecs);
             
            PGPKeyRingGenerator pgpKeyRingGen = PGPTools.createPGPKeyRingGenerator(
                    dsaKeyPair,
                    elGamalKeyPair,
                    "test@gmail.com",
                    "TestPass12345!".toCharArray()
                    );
             
            PGPTools.exportSecretKey(pgpKeyRingGen, new File(keysDir+File.separator+"secret.asc"), true);
            PGPTools.exportPublicKey(pgpKeyRingGen, new File(keysDir+File.separator+"public.asc"), true);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
     
    public static void main(String ... args) {
        new PGPCryptoBC();
    }
}
