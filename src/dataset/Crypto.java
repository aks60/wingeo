package dataset;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

//https://www.novixys.com/blog/how-to-generate-rsa-keys-java/
//https://gist.github.com/thomasdarimont/b05e3e785e088e35d37890480dd84364
public class Crypto {

    private static String algorithm = "DESede";
    private static byte[] encoded = {79, 12, 91, 62, 19, 71, 36, 84, 19, 63, 55, 89, 35, 27, 01, 82, 45, 64, 26, 95, 77, 83, 18, 90};
    public static String keyFile = "C:\\Temp\\crypto";

    public static void writeFileKeyPair() throws NoSuchAlgorithmException,
            FileNotFoundException, IOException {

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        Key pvt = kp.getPrivate();
        Key pub = kp.getPublic();

        var out = new FileOutputStream(keyFile + ".key");
        out.write(pvt.getEncoded());
        out.close();

        out = new FileOutputStream(keyFile + ".pub");
        out.write(pub.getEncoded());
        out.close();

        System.err.println("Private key format: " + pvt.getFormat());
        System.err.println("Public key format: " + pub.getFormat());
    }

    public static void readFileKeyPair() throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {

        Path path = Paths.get(keyFile);
        {
            byte[] bytes = Files.readAllBytes(path);
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey pvt = kf.generatePrivate(ks);
        }
        {
            byte[] bytes = Files.readAllBytes(path);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pub = kf.generatePublic(ks);
        }
    }

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

    public static void httpSynch() {
        try {
            //Загрузим файл
            URL url = Crypto.class.getResource("/resource/securety/crypto.pub");
            Path path = Paths.get(url.toURI());
            byte[] bytes = Files.readAllBytes(path);  

            //Получим ключ
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(ks);

            //Cлучайное сообщение
            SecureRandom random = new SecureRandom();
            String randomMes = new BigInteger(130, random).toString(32);
 //           String randomMes = new BigInteger(32, random).toString();

            //Шифровать случайное сообщение
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] randomMesBytes = randomMes.getBytes();
            byte[] encryptMesBytes = encryptCipher.doFinal(randomMesBytes); //закодированный 
            String encodeMesStr = Base64.getEncoder().encodeToString(encryptMesBytes);

            //Отправить на сервер закодированное случайное сообщение
//            var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/winnet/Crypto?action=secret&message=" + encodeMesStr)).build();
//            var client = HttpClient.newHttpClient();
//            HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();
//            HttpResponse<String> response = client.send(request, asString);

            //Полученное разшифрованное закр. ключом сообщение  
            System.out.println("MESSAGE3a = " + randomMes);
            System.out.println("MESSAGE2a = " + encodeMesStr);
            
            mesDecode(encodeMesStr);
           
        } catch (Exception e) {
            System.err.println("Ошибка: Crypto.httpSynch() " + e);
        }
    }

    public static void mesDecode(String encryptMesStr) throws Exception {
        byte[] decodeMesByte = Base64.getDecoder().decode(encryptMesStr);
        String decodedMesStr = new String(decodeMesByte);
        System.out.println("MESSAGE2c = " + decodedMesStr);

        //Загрузим файл
        URL url = Crypto.class.getResource("/resource/securety/crypto.key");
        Path path = Paths.get(url.toURI());
        byte[] bytes = Files.readAllBytes(path);

        //Получим ключ
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(ks);

        //Расшифровывать
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptMesBytes = decryptCipher.doFinal(decodedMesStr.getBytes());
        String decryptMesStr = new String(decryptMesBytes, StandardCharsets.UTF_8); //декодированный

        System.out.println("MESSAGE3c = " + decryptMesStr);
    }

    public static void httpAsync() throws ExecutionException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/winnet/Crypto?action=secret&username=sysdba"))
                //.uri(URI.create("https://postman-echo.com/post")) //тест запроса!!!
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("Hi there!"))
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        HttpClient client = HttpClient.newBuilder().executor(executor).build();

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        response.thenApply(res -> {
            System.out.printf("StatusCode: %s%n", res.statusCode());
            System.out.println("Version = " + res.version());
            System.out.println("Body = " + res.body());
            return res;
        })
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .get();

        executor.shutdownNow();
    }

    public static String random() {
        SecureRandom random = new SecureRandom();
        String rndstr = new BigInteger(130, random).toString(32);
        System.out.println(rndstr);
        return rndstr;
    }

// <editor-fold defaultstate="collapsed" desc="EXAMPLE">
    public static void httpSynch2() throws ExecutionException, InterruptedException, Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 8085)))
                .authenticator(Authenticator.getDefault())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                //.uri(URI.create("http://localhost:8080/winnet/Crypto?action=secret&username=sysdba"))
                .uri(URI.create("https://example.com")) //тест запроса!!!
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

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
