package common;

import sun.net.www.http.HttpClient;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Runtime.Version;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.time.Duration;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypto {

    private static String algorithm = "DESede";
    private static byte[] encoded = {79, 12, 91, 62, 19, 71, 36, 84, 19, 63, 55, 89, 35, 27, 01, 82, 45, 64, 26, 95, 77, 83, 18, 90};
    static String rndstr = "";

    public static void httpClient() {
        try {
            //Пара ключей RSA
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            //Шифровать 
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] secretMessageBytes = {79, 12, 91, 62, 19, 71, 36, 84, 19, 63, 55, 89, 35, 27, 01, 82, 45, 64, 26, 95, 77, 83, 18, 90};
            String secretMessage = new String(secretMessageBytes, StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes); //закодированный

            //Расшифровывать
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8); //декодированный

            //Сравнить
            System.out.println(secretMessage);
            if (secretMessage.equals(decryptedMessage)) {
                System.out.println(decryptedMessage);
            }

        } catch (InvalidKeyException e) {
            System.err.println("InvalidKeyException: " + e);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException: " + e);
        } catch (NoSuchPaddingException e) {
            System.err.println("NoSuchPaddingException: " + e);
        } catch (IllegalBlockSizeException e) {
            System.err.println("IllegalBlockSizeException: " + e);
        } catch (BadPaddingException e) {
            System.err.println("BadPaddingException: " + e);
        }
    }

    public static void httpRequest() {
        HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 8085)))
                .authenticator(Authenticator.getDefault())
                .build();
    }

    public static void rtwRandom() {
        SecureRandom random = new SecureRandom();
        String rndstr = new BigInteger(130, random).toString(32);
        System.out.println(rndstr);
    }

    public static void rtwConnect() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
