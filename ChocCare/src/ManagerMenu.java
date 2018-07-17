import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created by griffin on 11/14/2016.
 */
public class ManagerMenu extends OperatorMenu {
    @Override
    void mainMenu() {

        boolean isDone = false;
        String selection ;

        do {
            System.out.println("\nWelcome Manager, please choose one of the following options");
            System.out.println("l - Logout");
            System.out.println("o - Open Operator Menu");
            System.out.println("a - Generate All Reports");
            System.out.println("s - Generate Single Report");
            System.out.println("p - Provider Directory management");
            selection = Menu.promptString("Please choose an option:");

            switch(selection.toLowerCase()) {
                case "o":
                    super.mainMenu();
                    break;
                case "p":
                    updateDirectory();
                    break;
                case "s":
                    writeSingleReport();
                    break;
                case "a":
                    writeAllReports();
                    break;
                case "l":
                    isDone = true;
                    break;
                default:
                    System.out.println("Invalid input, please try again. \n");
            }

        } while(!isDone);


    }

    private void updateDirectory(){
        File target = new File("services.xml");
        ProviderDirectory theDirectory = null;
        try {
            theDirectory = new ProviderDirectory(target);
        } catch (IOException e) {
            System.out.println("Error with the provider directory file");
        }
        boolean isDone = false;
        int code; //Holds the desired Service Code to modify
        do {
            System.out.println("Welcome Manager, please choose one of the following options");
            System.out.println("c - Cancel");
            System.out.println("a - Add a new Service");
            System.out.println("u - Update a Service Code's Fees");
            System.out.println("t - Toggle a Service Code's Status");
            System.out.println("d - Display a Service Code");
            System.out.println("p - Display entire Provider Directory");

            String selection = Menu.promptString("Please choose an option:").toLowerCase();

            switch(selection) {
                case "p":
                    if (theDirectory != null) {
                        theDirectory.displayAll(System.out);
                    }
                    break;
                case "u": //update fee branch
                    code = Menu.promptInt("What is the service code?");
                    int newFee = Menu.promptInt("What is the new fee (In cents)");
                    if (theDirectory != null) {
                        theDirectory.updateFee(code, newFee);
                    }
                    break;
                case "t": //toggle service branch
                    code = Menu.promptInt("What is the service code?");
                    if (theDirectory != null) {
                        theDirectory.toggleActive(code);
                    }
                    break;
                case "d":
                    code = Menu.promptInt("What is the service code?");
                    if (theDirectory != null) {
                        theDirectory.display(code);
                    }
                    break;
                case "c": //back
                    isDone = true;
                    break;
                case "a": //Adding a new service
                    if (theDirectory != null) {
                        theDirectory.addService();
                    }
                    isDone = true;
                    break;
                default:
                    System.out.println("Invalid input, please try again. \n");
            }
        } while(!isDone);

        /* Lets save the modified directory */
        if (theDirectory != null) {
            theDirectory.save(target);
        }

    }

    private void writeSingleReport() {

        int numberID = -1;
        Date startDate;
        Date endDate;
        Customer theCustomer;
        PrintStream outputStream;

        try {
            numberID = Menu.promptInt("What Provider/Member ID would you like to generate a report for?");
            Optional optionalCustomer = Customer.findByID(numberID);

            if (optionalCustomer.isPresent()) {
                /* Get the start and end date from readDate method */
                startDate = readDate("start");
                endDate = readDate("end");
                /* Get's the Customer from the optional object which is assured as non empty*/
                theCustomer = (Customer) optionalCustomer.get();

                File rootFolder = new File("single reports");
                Util.ensureDirectoryExists(rootFolder, "single reports folder");

                File folder = new File(rootFolder, "" + numberID);
                Util.ensureDirectoryExists(folder, "single reports folder");

                File filepath = new File(folder, (new Date().getTime()/1000) + ".txt");

                outputStream = new PrintStream(filepath);
                theCustomer.writeReport(outputStream, startDate, endDate);
                outputStream.close();
            } else {
                System.out.println("Invalid ID number");
            }


        } catch (ParseException e) {
            System.out.println("Invalid input: " + e.getMessage());
            System.out.println("Returning to menu.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found for folder: " + numberID);
        } catch (IOException e) {
            System.out.println("An IO Error has occurred: " +  e.getMessage());
            System.out.println("Returning to menu.");
            //e.printStackTrace();
        }
    }

    /* Prompts the user for a date in the form of a string, and formats and returns a Date object */
    private Date readDate(String input) throws ParseException{

        String inputDate = Menu.promptString("Enter the " + input + " date in the form: MM-DD-YYYY");
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        return format.parse(inputDate);

    }

    private void writeAllReports() {
        Date startDate;
        Date endDate;
        try {
            /* Get the start and end date from readDate method */
            startDate = readDate("start");
            endDate = readDate("end");

            File filepath = new File("reports");
            Util.ensureDirectoryExists(filepath, "batch reports");

            PaymentSummary theSummary = Provider.writeAllReports(filepath, startDate, endDate);
            Member.writeAllReports(filepath, startDate, endDate);
            //Now use the returned PaymentSummary to generate the final report
            writeSummaryReport(theSummary);

        } catch (IOException e) {
            System.out.println("Error generating reports: Returning to Manager Menu");
        } catch (ParseException e) {
            System.out.println("Invalid date selected: Returning to Manager Menu");
        }

    }
    /*
    Tells the summary report to write the output to the screen, and asks the manager to verify.
    If verified it will call writeEFTData to write the EFT.txt file
    If not verified it will return to main menu without generating a text file
    */
    private void writeSummaryReport(PaymentSummary summary){
        Menu.promptString("\n\nSummary report is about to be displayed. Press any key to continue");
        summary.writeReport(System.out);

        String confirmation = Menu.promptString("EFT Data will now be created. Do you approve the above records? Y/N");
        if(confirmation.length() != 0 && ('Y' == confirmation.toUpperCase().charAt(0))) {
            writeEFTData(summary);
        }
        else {
            System.out.println("Records denied: Returning to Manager Menu");
        }



    }

    /**
     * This method sets up the Printstream for the EFT file and gives it to summary to write EFT data
     * @param summary The provided summary report that will write the EFT data
     */
    private void writeEFTData(PaymentSummary summary) {


        File folder = new File("EFT");
        Util.ensureDirectoryExists(folder, "EFT folder");
        File filepath = new File(folder, "EFT.txt");
        try {
            PrintStream fileStream = new PrintStream(filepath);
            summary.writeReport(fileStream);
            fileStream.close();
            System.out.println("EFT Data created. Returning to Main Menu.");
        } catch (FileNotFoundException e) {
            System.out.println("File write error: Returning to Main Menu.");
        }


    }


}
