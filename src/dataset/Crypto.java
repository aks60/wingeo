package dataset;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Crypto {

    //private static String algorithm = "DESede";
    //private static byte[] encoded = {79, 12, 91, 62, 19, 71, 36, 84, 19, 63, 55, 89, 35, 27, 01, 82, 45, 64, 26, 95, 77, 83, 18, 90};
    //static String rndstr = "";
    
    //https://gist.github.com/thomasdarimont/b05e3e785e088e35d37890480dd84364
    public static void httpCrypto() {
        try {
            //Пара ключей RSA
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            System.out.println(List.of(privateKey.getEncoded()));
            System.out.println(List.of(publicKey.getEncoded()));
            
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

    /**
     * Пример отправки синхронного POST-запроса
     *
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public static void httpSynch() throws ExecutionException, InterruptedException, Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 8085)))
                .authenticator(Authenticator.getDefault())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/winnet/Crypto?action=secret&username=sysdba"))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    //https://gist.github.com/thomasdarimont/b05e3e785e088e35d37890480dd84364
    public static void httpAsync() throws ExecutionException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/winnet/Crypto?action=secret&username=sysdba"))
                //                .uri(URI.create("https://postman-echo.com/post"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("Hi there!"))
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        HttpClient client = HttpClient.newBuilder().executor(executor).build();

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(res -> {
            System.out.printf("StatusCode: %s%n", res.statusCode());
            //System.out.println("Version = " + res.version());
            //System.out.println("Body = " + res.body());
            return res;
        })
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .get();

        executor.shutdownNow();
    }

    public static void random() {
        SecureRandom random = new SecureRandom();
        String rndstr = new BigInteger(130, random).toString(32);
        System.out.println(rndstr);
    }

// <editor-fold defaultstate="collapsed" desc="EXAMPLE">
    public void get(String uri) throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    public void get2(String uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/get"))
                .GET()
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/get"))
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();

        byte[] sampleData = "Sample request body".getBytes();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofByteArray(sampleData))
                .build();

        //HttpResponse<String> response = client.send(request, HttpResponse.BodyHandler.asString());
    }

// </editor-fold>    
}
