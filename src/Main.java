import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //define variables
        Scanner sc = new Scanner(System.in);
        //scan/check
        String action = "";
        String key;
        //path we apply action to
        String path;

        //choose between scan an check
        System.out.print("Choose action (scan/check): ");
        while (!(Objects.equals(action, "scan") || Objects.equals(action, "check"))){
            action = sc.nextLine();
            if(!(Objects.equals(action, "scan") || Objects.equals(action, "check"))) {
                System.out.println("The option you chose is not correct");
                System.out.print("Choose action (scan/check): ");
            }
        }
        System.out.println(action + " was selected");

        System.out.print("Choose key: ");
        key = sc.nextLine();
        System.out.println("Chosen key :" + key);

        System.out.print("Scan path: ");
        path = sc.nextLine();
        System.out.println("Path to be scanned :" + path);

    }
}