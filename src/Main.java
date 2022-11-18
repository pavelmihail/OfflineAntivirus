import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        //database
        HashMap<String, String> dataBase = new HashMap<>();
        //define variables
        Scanner sc = new Scanner(System.in);
        //scan/check
        String action = "";
        String key;
        //path we apply action to
        String path;

        //choose between scan an check
        System.out.print("Choose action (scan/check): ");
        while (!(Objects.equals(action, "scan") || Objects.equals(action, "check"))) {
            action = sc.nextLine();
            if (!(Objects.equals(action, "scan") || Objects.equals(action, "check"))) {
                System.out.println("The option you chose is not correct");
                System.out.print("Choose action (scan/check): ");
            }
        }
        System.out.println(action + " was selected");

        System.out.print("Choose key: ");
        key = sc.nextLine();
        System.out.println("Chosen key :" + key);

        System.out.print("Scan/check path: ");
        path = sc.nextLine();
        System.out.println("Path to be scanned/checked :" + path);

        File pathFileDir = new File(path);
        File hashmacs = new File("scanHash.txt");
        File reportFile = new File("reportFile.txt");

        updateDataBase(dataBase, reportFile);

        if (action.equals("scan") && pathFileDir.isDirectory()) {

            FileWriter flHashMacs = new FileWriter(hashmacs);
            PrintWriter prHashMacs = new PrintWriter(flHashMacs);

            FileWriter flReportFile = new FileWriter(reportFile);
            PrintWriter pwReportFile = new PrintWriter(flReportFile);

            if (pathFileDir.exists() && pathFileDir.isDirectory()) {
                File[] files = pathFileDir.listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        prHashMacs.println(file.getName() + " " + getHex(getHmac(file.getAbsolutePath(), key)));
                        dataBase.put(file.getName(), "NEW");
                    }
                }
            }

            //print all elements from hasmap in the report file
            for (String fileName : dataBase.keySet()) {
                pwReportFile.println(fileName + " " + dataBase.get(fileName));
            }

            prHashMacs.close();
            pwReportFile.close();
        } else if (action.equals("check") && pathFileDir.isFile()) {

            boolean foundFile = false;
            String[] splitedStr = {"", ""};

            //file reader from hasmacs
            FileReader fr = new FileReader(hashmacs);
            BufferedReader br = new BufferedReader(fr);

            //file writer in report
            FileWriter flReportFile = new FileWriter(reportFile);
            PrintWriter pwReportFile = new PrintWriter(flReportFile);

            //string with the corresponding red line
            String readLine = br.readLine();

            //the hash value we want to verify
            String hashValue = getHex(getHmac(path, key));

            //read until eof
            while (readLine != null) {
                splitedStr = readLine.split("\\s+");
                readLine = br.readLine();

                //check if there is the selected file in the report
                if (splitedStr[0].equals(pathFileDir.getName())) {
                    foundFile = true;
                    break;
                }
            }

            if (!foundFile) {
                System.out.println("The file tou want to check is not in the scan folder");
                System.out.println("Please try scanning again");
            } else {
                if (hashValue.equals(splitedStr[1])) {
                    dataBase.put(pathFileDir.getName(), "OK");
                } else {
                    dataBase.put(pathFileDir.getName(), "CORRUPTED");
                }
            }

            //print all elements from hasmap in the report file
            for (String fileName : dataBase.keySet()) {
                pwReportFile.println(fileName + " " + dataBase.get(fileName));
            }

            br.close();
            pwReportFile.close();
        } else {
            System.out.println("It seems the path you have introduced is not compatible with the action you have chosen");
        }
    }

    public static byte[] getHmac(String fileName, String key) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");

        Mac mac = Mac.getInstance("HmacSHA512");

        mac.init(keySpec);

        byte[] out = mac.doFinal(bis.readAllBytes());

        fis.close();
        return out;
    }

    public static String getHex(byte[] values) {
        StringBuilder sb = new StringBuilder();
        for (byte value : values) {
            sb.append(String.format("%02x", value));
        }
        return sb.toString();
    }

    public static void updateDataBase(HashMap<String, String> db, File file) throws IOException {

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String readLine = br.readLine();

        while (readLine != null) {
            String[] buffer = readLine.split("\\s+");
            db.put(buffer[0], buffer[1]);
            readLine = br.readLine();
        }
    }

    public static void printHashMap(HashMap<String, String> hm) {
        for (String fileName : hm.keySet()) {
            System.out.println(fileName + " " + hm.get(fileName));
        }
    }
}