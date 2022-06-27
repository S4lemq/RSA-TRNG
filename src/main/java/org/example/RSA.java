package org.example;

import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RSA{
    private final BigInteger P;
    private final BigInteger Q;
    private BigInteger N;
    private final BigInteger PHI;
    private BigInteger e;
    private final PrimeService primeService;

    public RSA(int keySize) {
        List<Integer>  randomNumbers = new ArrayList<>();
        primeService = new PrimeService(randomNumbers);
        P = new BigInteger(String.valueOf(primeService.makePrime(keySize)));
        Q = new BigInteger(String.valueOf(primeService.makeDifferentPrime(P)));
        PHI = P.subtract(BigInteger.ONE).multiply(Q.subtract(BigInteger.ONE));
    }

    private String generatePublicKey(int keySize){
        N = P.multiply(Q);
        e = new BigInteger(String.valueOf(primeService.makePrime(keySize/2)));
        while (PHI.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(PHI) < 0) {
            e.add(BigInteger.ONE);
        }
        return N + "," + e;
    }

    private String generatePrivateKey(){
        BigInteger d;
        d = e.modInverse(PHI);
        return N + "," + d;
    }

    private static String byteToString(byte[] cipher) {
        StringBuilder temp = new StringBuilder();
        for (byte val : cipher){
            temp.append(val);
        }
        return temp.toString();
    }

    private int getPosition(String key){
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i)==',') {
                return i;
            }
        }
        return -1;
    }

    public String getHash(String message) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                message.getBytes(StandardCharsets.UTF_8)
        );
        return new String(Hex.encode(hash));
    }

    public byte[] arrayStringToByte(String message, int length){
        StringBuilder val = new StringBuilder();
        byte[] arr = new byte[length];
        int j = 0;
        for (int i = 0; i < message.length(); i++) {
            if(message.charAt(i)==32){
                continue;
            }
            if(message.charAt(i)!=','){
                val.append(message.charAt(i));
            }else{
                arr[j] = Byte.parseByte(val.toString());
                j++;
                val = new StringBuilder("");
            }
        }
        return arr;
    }

    //zaszyfruj
    public byte[] encryptMessage(byte[] message, String publicKey) {
        int position = getPosition(publicKey);
        BigInteger key_N = new BigInteger(publicKey.substring(0,position));
        BigInteger key_E = new BigInteger(publicKey.substring(position+1));
        return (new BigInteger(message)).modPow(key_E, key_N).toByteArray();
    }

    //odszyfruj
    public byte[] decryptMessage(byte[] message, String privateKey) {
        int position = getPosition(privateKey);
        BigInteger key_N = new BigInteger(privateKey.substring(0,position));
        BigInteger key_D = new BigInteger(privateKey.substring(position+1));
        return (new BigInteger(message)).modPow(key_D, key_N).toByteArray();
    }

    public static void main (String [] arguments) throws NoSuchAlgorithmException {
        System.out.println("Podaj długość klucza: ");
        Scanner scanner = new Scanner(System.in);
        int keySize = scanner.nextInt();
        scanner.nextLine();

        RSA rsa = new RSA(keySize);

        System.out.println("podaj tekst do podpisu: ");
        String inputString = scanner.nextLine();

        String hashMessage = rsa.getHash(inputString);
        System.out.println("Hash: " + hashMessage);

        String publicKey = rsa.generatePublicKey(keySize);
        System.out.println("Klucz publiczny: " + publicKey);
        String privateKey = rsa.generatePrivateKey();
        System.out.println("Klucz prywatny: " + privateKey);

        System.out.println("szyfrowanie podpisu: " + inputString);
        System.out.println("Reprezentacja bitowa hashu przed szyfrowaniem: "
                + byteToString(hashMessage.getBytes()));

        byte[] cipher = rsa.encryptMessage(hashMessage.getBytes(), publicKey);

        //System.out.println("zaszyfrowany tekst: " + );

        System.out.println("podaj klucz prywatny: ");
        String myKey = scanner.nextLine();

        //-------------------------sprawdź wiadomość-------------------------------------------------
//        System.out.println("cipher: " + Arrays.toString(cipher));
//        System.out.println("Podaj wiadomość zaszyfrowaną: ");
//        String secretMessage = scanner.nextLine();
//        byte[] plain = rsa.decryptMessage(rsa.arrayStringToByte(secretMessage, cipher.length), myKey);
        //----------------------------------------------------------------------------------------

       byte[] plain = rsa.decryptMessage(cipher, myKey);

        System.out.println("Reprezentacja bitowa tekstu po odszyfrowaniu: " + byteToString(plain));
        System.out.println("Odszyfrowany tekst: " + new String(plain));

        System.out.println("Czy podpis prawidłowy: " + hashMessage.equals(new String(plain)));
    }
}
