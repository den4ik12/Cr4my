import org.apache.commons.codec.binary.Base32;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Base32 base32 = new Base32();
        GammaEncryptor gammaEncryptor = new GammaEncryptor();
        gammaEncryptor.init("src/lcg-keys.txt", "src/lfsr-connectionvector.txt");
        String text = base32.encodeAsString("Gangster my hello".getBytes());
        String encrypt = gammaEncryptor.encrypt(text);
        String decrypt = gammaEncryptor.encrypt(encrypt);
        System.out.println();
        System.out.println("MAIN RESULT");
        System.out.println("Зашифрованный текст:" + encrypt);
        System.out.println("Расшифрованный текст :" + new String(base32.decode(decrypt), StandardCharsets.UTF_8));
    }
    public static int pow(int value, int powValue) {
        return (int) Math.pow(value, powValue);
    }

}