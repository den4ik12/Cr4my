import org.apache.commons.codec.binary.Base32;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GammaEncryptor {


    private long seed;
    private long a;
    private long b;
    private long m;
    private String initConnectionVector;
    private String registerVector;

    public void init(String lcgPath, String lfsrPath) throws IOException {
        String[] lcdKeys = Files.readAllLines(Path.of(lcgPath)).toArray(new String[4]);
        String[] lfsrKeys = Files.readString(Path.of(lfsrPath)).split("\r\n");
        seed = Long.parseLong(lcdKeys[0]);
        a = Long.parseLong(lcdKeys[1]);
        b = Long.parseLong(lcdKeys[2]);
        m = Long.parseLong(lcdKeys[3]);
        registerVector = lfsrKeys[0];
        initConnectionVector = lfsrKeys[1];
    }

    public String encrypt(String text) {

        LinearCongruentGenerator linearCongruentGenerator = new LinearCongruentGenerator(seed, a, b, m);
        LFSRGenerator lfsrGenerator = new LFSRGenerator();
        lfsrGenerator.registerVector = Arrays.stream(registerVector.split(""))
                .map(c -> Integer.parseInt(c))
                .collect(Collectors.toCollection(LinkedList::new));

        int iterationNum = 1;
        StringBuilder resultStringBuilder = new StringBuilder();
        StringBuilder totalGamma = new StringBuilder();
        while (text.length() > 0) {

            //скопировать либо целый кусок, либо остаток
            int symbolsCountToTCopy = Math.min(text.length(), 8);
            //скопировали
            String substringToEnc = text.substring(0, symbolsCountToTCopy);
            //убрали нач кусок
            text = text.substring(symbolsCountToTCopy);

            StringBuilder tCopy = new StringBuilder();

            //проходим по буквам
            for (String letter : substringToEnc.split("")) {
                //перевели букву в двоич
                StringBuilder encLetter = new StringBuilder(stringToBinary(letter));
                //если остаток - заполняем нулями
                while (encLetter.length() < 8) {
                    encLetter.insert(0, "0");
                }
                tCopy.append(encLetter);
            }

            //если остаток - заполняем нулями
            while (tCopy.length() < 64) {
                tCopy.append("0");
            }


            //7 bits
            String lfsrBits = lfsrGenerator.makeIteration(initConnectionVector);
            System.out.println("Значение регистра = " + lfsrBits);

            long lfsrLong = Long.parseLong(lfsrBits, 2);
            System.out.println("Значение регистра long = " + lfsrLong);

            linearCongruentGenerator.currentSeed = lfsrLong;
            Base32 base32 = new Base32();
            StringBuilder gammaBuilder = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                long num1 = linearCongruentGenerator.next();
                System.out.println("lgg next long = " + num1);
                StringBuilder num1Binary = new StringBuilder(Long.toBinaryString(num1));
                while (num1Binary.length() < 16) {
                    num1Binary.insert(0, "0");
                }
                System.out.println("lgg binary = " + num1Binary);
                gammaBuilder.append(num1Binary);
                System.out.println("Current gamma = " + gammaBuilder);
            }

            String gamma = gammaBuilder.toString();

            totalGamma.append(gamma);

            //xor гаммы и куска
            String resPart = xorTwoStrings(tCopy.toString(), gamma);
            resultStringBuilder.append(resPart);

            System.out.println("\nIteration " + (iterationNum++));
            System.out.println("TEXT :" + tCopy);
            System.out.println("GAMMA:" + gamma);
            System.out.println("XOR  :" + resPart);
        }

        String result = resultStringBuilder.toString();
        StringBuilder stringBuilder = new StringBuilder();

        List<String> split = Arrays.stream(result.split("(?<=\\G.{8})")).toList();
        for (String binaryLetter : split) {
            char encodedSymbol = (char) Long.parseLong(binaryLetter, 2);
            stringBuilder.append(encodedSymbol);
        }

        System.out.println("=========RESULT=========");
        System.out.println("RESULT     : " + result);
        System.out.println("TOTAL GAMMA: " + totalGamma);
        System.out.println("Исходный текст: " + stringBuilder);

        return stringBuilder.toString();
    }


    private static String stringToBinary(String s) {
        return s.chars()
                .collect(StringBuilder::new,
                        (sb, c) -> sb.append(Integer.toBinaryString(c)),
                        StringBuilder::append)
                .toString();
    }

    private static String xorTwoStrings(String first, String second) {
        if (first.length() != second.length())
            throw new RuntimeException("Different lengths");

        List<String> firstList = Arrays.stream(first.split("")).toList();
        List<String> secondList = Arrays.stream(second.split("")).toList();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < first.length(); i++) {
            sb.append(Integer.parseInt(firstList.get(i)) ^ Integer.parseInt(secondList.get(i)));
        }

        return sb.toString();
    }
}
