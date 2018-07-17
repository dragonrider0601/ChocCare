import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Menu class
 * Created by gstone on 11/10/2016.
 */
abstract class Menu {

    static Scanner input = new Scanner(System.in);
    abstract void mainMenu();

    public static void main(String[] args) throws IOException {
        main();
    }

    private static void demonstrateGetResource() throws IOException {
        Scanner scanner = new Scanner(Menu.class.getResource("testData.txt").openStream());
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }
        scanner.close();
    }

    public static void main() {

        int isDone = 0;
        while (isDone == 0) {
            System.out.println("\nWhich Menu would you like to open?"); //Fadi - add a \n to that line so it matches Randy and Mine
            System.out.println("\ne - exit"); //Fadi - add a \n to that line so it matches Randy and Mine
            System.out.println("o - Operator Menu");
            System.out.println("b - Billing Menu");
            System.out.println("m - Manager Menu");

            //Changed to promptChar, allowing the user to enter "operator" and have their input accepted --G
            char choice = promptChar("");
            Menu menu = null;
            switch (choice) {
                case 'o':
                    menu = new OperatorMenu();
                    break;
                case 'b':
                    menu = BillingMenu.providerLogin();
                    break;
                case 'm':
                    menu = new ManagerMenu();
                    break;
                case 'e':
                    System.out.println("Goodbye.");
                    isDone = 1;
                    break;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
            //Removed redundant menuOpened variable --G
            if (menu != null) menu.mainMenu();
        }

    }

    protected static String promptString(String message) {

        System.out.println(message);
        String userInput = input.nextLine();
        return userInput;
    }

    protected static int promptInt(String message) {
        System.out.println(message);
        while(!input.hasNextInt()) {
            input.next();
            System.out.println("Enter a number less than 1000000000");
        }
        int userInput = input.nextInt();
        input.nextLine(); // Added by Randy to skip over \n
        return userInput;
    }

    protected static long promptLong(String message) {
        System.out.println(message);
        while(!input.hasNextLong()) {
            input.next();
            System.out.println("Please input a number.");
        }
        long userInput = input.nextLong();
        input.nextLine(); // Added by Randy to skip over \n
        return userInput;
    }

    protected static char promptChar(String message) {
        System.out.println(message);
        String userInput;
        do {
            userInput = input.nextLine();
        } while (userInput.isEmpty());
        return Character.toLowerCase(userInput.charAt(0));
    }
}
