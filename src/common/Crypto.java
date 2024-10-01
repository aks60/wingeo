package common;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypto {

    private static String algorithm = "DESede";
    private static byte[] encoded = {79, 12, 91, 62, 19, 71, 36, 84, 19, 63, 55, 89, 35, 27, 01, 82, 45, 64, 26, 95, 77, 83, 18, 90};
    static String rndstr = "";

    public static void rtwRandom() {
        SecureRandom random = new SecureRandom();
        String rndstr = new BigInteger(130, random).toString(32);
        System.out.println(rndstr);
    }
    
    public static void rtwConnect() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
