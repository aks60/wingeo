package dataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpTimeoutException;
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
import javax.swing.JOptionPane;
import startup.Test;

//https://www.novixys.com/blog/how-to-generate-rsa-keys-java/
//https://gist.github.com/thomasdarimont/b05e3e785e088e35d37890480dd84364
public class Crypto {

    public static String keyFile = "C:\\Temp\\crypto";

    public Crypto() {
    }

    //Генерация ключей
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
            byte[] secretMesBytes = {79, 12, 91, 62, 19, 71, 36, 84, 19, 63, 55, 89, 35, 27, 01, 82, 45, 64, 26, 95, 77, 83, 18, 90};
            String secretMesStr = new String(secretMesBytes, StandardCharsets.UTF_8);
            byte[] encryptedMesBytes = encryptCipher.doFinal(secretMesBytes);
            String encodedMesStr = Base64.getEncoder().encodeToString(encryptedMesBytes); //закодированный

            //Расшифровывать
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedMesBytes = decryptCipher.doFinal(encryptedMesBytes);
            String decryptedMesStr = new String(decryptedMesBytes, StandardCharsets.UTF_8); //декодированный

            //Сравнить
            System.out.println(secretMesStr);
            System.out.println(decryptedMesStr);
            if (secretMesStr.equals(decryptedMesStr)) {
                System.out.println("УРА АКСЁНОВ");
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

    public void httpAsync(String server) {
        try {
            //Загрузим файл
            InputStream in = getClass().getResourceAsStream("/resource/securety/crypto.pub");
            byte[] bytes = in.readAllBytes();
            
            //Получим ключ
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(ks);
            
            //Cлучайное сообщение
            SecureRandom random = new SecureRandom();
            String randomMes = new BigInteger(130, random).toString(32);

            //Шифровать случайное сообщение
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] randomMesBytes = randomMes.getBytes("UTF-8");
            byte[] encryptMesBytes = encryptCipher.doFinal(randomMesBytes); //закодированный 
            String encodeMesStr = Base64.getEncoder().encodeToString(encryptMesBytes);

            //Отправить на сервер закодированное случайное сообщениеtry 
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + server + ":8080/winnet/Crypto?action=secret&message=" + encodeMesStr))
                    .timeout(Duration.ofSeconds(16)).build();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            HttpClient client = HttpClient.newBuilder().executor(executor).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(res -> {

                //Проверка сервера
                if (randomMes.equals(res.body().trim())) {
                    Conn.setHttpcheck(true);
                    //System.out.println("УРА АКС");
                }
                return res;
            }).get();
            executor.shutdownNow();

        } catch (HttpTimeoutException e) {
            System.err.println("Ошибка: Crypto.httpAsync() №1 " + e);
        } catch (ExecutionException e2) {
            System.err.println("Ошибка: Crypto.httpAsync() №2 " + e2);
        } catch (InterruptedException e3) {
            System.err.println("Ошибка: Crypto.httpAsync() №3 " + e3);
        } catch (Exception e4) {
            System.err.println("Ошибка: Crypto.httpAsync() №4 " + e4);
        }
    }

// <editor-fold defaultstate="collapsed" desc="EXAMPLE">
    public static void readFileKeyPair() throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        {
            Path path = Paths.get(keyFile + ".key");
            byte[] bytes = Files.readAllBytes(path);
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey pvt = kf.generatePrivate(ks);
        }
        {
            Path path = Paths.get(keyFile + ".pub");
            byte[] bytes = Files.readAllBytes(path);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pub = kf.generatePublic(ks);
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

            //Шифровать случайное сообщение
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] randomMesBytes = randomMes.getBytes("UTF-8");
            byte[] encryptMesBytes = encryptCipher.doFinal(randomMesBytes); //закодированный 
            String encodeMesStr = Base64.getEncoder().encodeToString(encryptMesBytes);

            //Отправить на сервер закодированное случайное сообщение
            var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/winnet/Crypto?action=secret&message=" + encodeMesStr)).build();
            var client = HttpClient.newHttpClient();
            HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, asString);

            if (randomMes.equals(response.body().trim())) {
                System.out.println("httpSynch УРА ТЫ ГЕНИЙ");
            }

            //Полученное разшифрованное закр. ключом сообщение  
            //System.out.println("httpSynch2 = " + randomMes);
            //System.out.println("httpSynch3 = " + response.body());
            //testServer(encodeMesStr);
        } catch (Exception e) {
            System.err.println("Ошибка: Crypto.httpSynch() " + e);
        }
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

// </editor-fold>    
}
