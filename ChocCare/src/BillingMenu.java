/*
BillingMenu is a derived class from Menu. The class collects billing data from the provider and ProviderDirectory.
Data collected from the provider includes the date of service provided, provider number, member number,
and any comments the provider wishes to add. The class gathers the service code and service fee from ProviderDirectory.
The class also handles provider terminal login and retrieves membership validity.
Billing data is gathered, stored in a ServiceRecord object, and then sent to ServerConnection.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.text.ParseException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Created by randyrollofson on 11/13/16.
 */

//This class handles billing operations by the provider
public class BillingMenu extends Menu {
    private int providerNumber;
    private ServerConnection serverConnection = null;

    //Constructor or with args
    public BillingMenu(int provNum) {
        providerNumber = provNum;
    }

    //This method is called from Menu() which invokes the BillingMenu constructor.
    //Gets provider number. Can return null if provider number is invalid.
    public static BillingMenu providerLogin() {
        int provNum = 0;
        boolean isDone = false;
        boolean isMenu = false;

        //checks if input is an integer
        do {
            try {
                provNum = promptInt("Enter your 9-digit provider number: ");
                isDone = true;
                }
            catch(InputMismatchException e) {
                System.out.println("\nThis is not an integer, try again!" + e.getMessage());
                }
                //TODO temporarily disabled (prints a message but lets you in anyway) //Completed by RR
        } while (!isDone);

        //TODO validate provider number //Completed by RR
        ServerConnection newConnection = null;
        Optional<Customer.Status> status = newConnection.checkStatus(provNum);
        Optional optionalCustomer = Customer.findByID(provNum);
        if (optionalCustomer.isPresent() && status.isPresent()) {
            if (status.orElse(Customer.Status.EXPIRED).isProvider()) {
                System.out.println("VALID");
                isMenu = true;
            }
        }
        else {
            System.out.println("INVALID");
            isMenu = false;
        }
        if (isMenu) {
            return new BillingMenu(provNum); //Changed --> constructor --Griffin
        }
        else {
            return null;
        }
    }

    //Overrides Menu.mainMenu
    //Builds menu for provider's billing options
    @Override
    public void mainMenu() {
        boolean isDone = false;

        while (!isDone) {
            System.out.println("\nWelcome Provider, please choose one of the following options:");
            System.out.println("\nl - Logout"); //Fadi-- switched the order so that logout is at the top.
            System.out.println("v - Validate Member");
            System.out.println("s - Look-Up Service Code");
            System.out.println("b - Bill ChocAn");

            switch (getUserInput()) {
                case 'v': {
                    int memberNumber = getMemberNumber();
                    validateMember(memberNumber);
                    break;
                }
                case 's': {
                    lookUpServiceCode();
                    break;
                }
                case 'b': {
                    collectBillingData();
                    break;
                }
                case 'l': {
                    System.out.println("Goodbye.");
                    isDone = true;
                    break;
                }
                default: {
                    System.out.println("Invalid input!");
                    break;
                }
            }//end switch statement
        }//end while loop
    }


    private char getUserInput(){
        return promptChar("\nPlease choose an option:"); //ask the user for an input
    }

//ServiceRecord is getting the current date and time. Leaving this here just in case
/*
    public Date getCurrentDateTime() {

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));

        return dateobj;
    }
*/

    //Gets member number
    public int getMemberNumber() {
        int memberNumber = 0;
        boolean isDone = false;

        do {
            try {
                memberNumber = promptInt("Enter the 9-digit member number: ");
                isDone = true;
            }
            catch(InputMismatchException e) {
                System.out.println("\nThis is not an integer, try again!" + e.getMessage());
            }
        } while (!isDone);

        return memberNumber;
    }

    //Validates member number
    public void validateMember(int memberNumber) {
        //TODO: status can be valid, invalid, or not found--should have different message for each! //Completed by RR
        Optional<Customer.Status> status = serverConnection.checkStatus(memberNumber);
        if (status.isPresent()) {
            if (status.orElse(Customer.Status.EXPIRED).isActive()) {
                System.out.println("VALID");
            } else {
                System.out.println("INVALID");
            }
        }
        else {
            System.out.println("NOT FOUND");
        }
    }

    //Gets date of service
    public Date getServiceDate() {
        Date serviceDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        String dateInString;
        boolean isDone = false;
        do {
            dateInString = promptString("Enter the Date of Service in the form: MM-DD-YYYY: ");
            try {
                serviceDate = formatter.parse(dateInString); //coverts String into Date object
                isDone = true;
            }
            catch (ParseException e) {
                System.out.println("Invalid format " + e.getMessage());
            }
        } while (!isDone);
        return serviceDate;
    }

    //Displays Provider Directory based on service code
    public void lookUpServiceCode() {

        ProviderDirectory providerDirectory = ServerConnection.getProviderDirectory(); //modified by sam
        //providerDirectory.displayAll(System.out);
        int code = Menu.promptInt("Enter the 6-digit service code");
        providerDirectory.display(code);
    }

    //The provider has the option to enter up to 100 characters of comments
    public String addComments() {
        final int maxLength = 100;
        String comments;
        String choice;
        do {
            choice = promptString("Would you like to add comments (100 character max)?: (y/n)").toLowerCase();
            if (choice.equals("y")) {
                comments = promptString("Enter comments:");
                if (comments.length() > maxLength) {
                    System.out.println("Comments must be under 100 characters!");
                }
            }
            else {
                comments = "No comments";
            }
        } while (comments.length() > maxLength);

        return comments;
    }

    //Collects billing data, creates service record object, sends object to serverConnection, displays fee
    public void collectBillingData() {
        boolean isValid = false;
        Date serviceDate = getServiceDate();
        int memberNumber;
        Optional<Customer.Status> status;
        do {
            memberNumber = getMemberNumber();
            status = serverConnection.checkStatus(memberNumber);
            if (status.isPresent()) {
                if (status.orElse(Customer.Status.EXPIRED).isActive()) {
                    System.out.println("VALID");
                    isValid = true;
                } else {
                    System.out.println("INVALID");
                }
            } else {
                System.out.println("NOT FOUND");
            }
        } while (!isValid);
        int serviceCode;
        //TODO get rid of all these  Currently not working  comments once we verify that they are indeed working
        ProviderDirectory providerDirectory = ServerConnection.getProviderDirectory(); //gets provider directory
        //providerDirectory.displayAll(System.out); //displays provider directory

        boolean correct;
        do {
            serviceCode = promptInt("Enter 6-digit Service Code:");
            //Added isActive check --Griffin
            correct = providerDirectory.isActive(serviceCode);
            if (correct) {
                providerDirectory.display(serviceCode); //displays a single service, service code, and fee.
                //promptString --> promptChar --Griffin
                correct = promptChar("Is this correct? (y/n)") == 'y';
            } else {
                System.out.println("Service code not found.");
            }
        } while (!correct);
        int serviceFee = providerDirectory.getFee(serviceCode);

        String comments = addComments();

        //displays are for testing purposes only
        DateFormat date = new SimpleDateFormat("MM-dd-yyyy");
        System.out.println("Service Date:");
        System.out.println(date.format(serviceDate));
        System.out.println("Provider Number:");
        System.out.println(providerNumber);
        System.out.println("Member Number:");
        System.out.println(memberNumber);
        System.out.println("Service Code:");
        System.out.println(serviceCode);
        System.out.println("Comments:");
        System.out.println(comments);

        //this display is not just for testing purposes. It will display always
        System.out.println("\nService Fee:");
        System.out.println(serviceFee);

        ServiceRecord serviceRecord = new ServiceRecord(serviceDate, providerNumber, memberNumber, serviceCode, serviceFee, comments);
        try {
            ServerConnection.submitServiceRecord(serviceRecord); //Added --G
        } catch (IOException e) {
            System.out.println("There was an error submitting the service record: " + e.getMessage());
        }
    }

    //returns provider number
    public int getProviderNumber() {
        return providerNumber;
    }

}

