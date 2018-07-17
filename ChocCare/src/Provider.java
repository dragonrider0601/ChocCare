//Fadi Labib
//CS300 - Fall 2016
//ChocAn Software
//Started: 11/11/2016 6:00 PM
//Ended: 11/16/2016 10:26 AM

import java.io.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/* Provider is a derived class from the abstract base class "Customer". It has the same functionality of the customer class, plus some
additional methods implementation specific to a provider. There is a txt file titled "maxProviderNumber.txt" and it contains a single number
which is the last number used for a provider.  */
public class Provider extends Customer {
    static final int lowestProviderNum = 1000;

    /* Creates a provider and assign them an automatic ID and checks if their Status is a valid provider status */
    public Provider(String name, String address, String city, String state, String zip, Status status) throws IOException {
        super(name, reserveNextProviderNumber(), address, city, state, zip, status);
        if (!status.isProvider()) {
            throw new RuntimeException("Provider Status is NOT_VALID");
        }//end if
    }//end constructor

    /* Create a provider from file */
    public Provider(File target) throws IOException {
        super(target);
    }//end file constructor

    /* Checks if a provider status assigned is valid and return true or false accordingly */
    @Override
    public boolean isValidStatus(Status status) {
        return status.isProvider()&& super.isValidStatus(status);
    }

    /* Gets the last used provider number from a text file and returns it */
    static int getMaxProviderNumber() throws FileNotFoundException {
        Scanner input; //A scanner input that starts as null
        int maxProviderNumber; //local variable to store the current maxProviderNumber
        try {
            //open the maxProviderNumber.txt
            input = new Scanner(new File("maxProviderNumber.txt"));
            maxProviderNumber = input.nextInt();
            input.close();
        //catch error in the case the file is not found and create the file
        } catch (FileNotFoundException e) {
            maxProviderNumber = lowestProviderNum - 1;
        }//end catch block

        //return the value stored in file which is the maxProviderNumber that has last been used
        return maxProviderNumber;
    }//end getMaxProviderNumber

    /* Finds the next provider number to be used when a new member is added */
    static int reserveNextProviderNumber() throws IOException {
        int currentMaxNum = getMaxProviderNumber(); //temp value that stores last used provider number
        ++currentMaxNum; //increment that number by 1

        //write the new number to the file, which will be the current max number (replaces the current max number in file)
        PrintWriter write;
        write = new PrintWriter("maxProviderNumber.txt");
        write.println(currentMaxNum);
        write.close();

        //return the new increment value and this is the current provider number to be used.
        return currentMaxNum;
    }//end reserveNextProviderNumber

    /* Call Customer write report and adds a line to the top of it indicating that it's a provider report*/
    @Override
    public void writeReport(PrintStream target, Date startDate, Date endDate) throws IOException {
        target.println("====Provider Report====");
        super.writeReport(target, startDate, endDate);
    }//end writeReport

    /* A variant of the write report that not only writes the report but also returns a Provider payment object containing the
    * provider name, id, the number of services they offered and the total fees for those services. Only those services that
     * are within the pounds of the date range are included */
    public ProviderPayment writeProviderReport(PrintStream target, Date startDate, Date endDate) throws IOException {
        writeReport(target, startDate, endDate); //calls the regular write report from before
        int count = 0; //number of services provided that time period
        int totalFees = 0; //total cost of service provided in cents
        List<BigInteger> recordNumbers = serviceRecords; //list of service records
        //a for loop that goes through the elements inside the record numbers list
        for (BigInteger recordNumber : recordNumbers) {
            //create a new local object of that service record by using the ServiceRecord constructor from file
            /* the file argument passed in to the ServiceRecord file constructor is a function that returns a service record
            * that is already written and use to construct my local service record object newRecord */
            ServiceRecord newRecord = new ServiceRecord(Util.getServiceRecordFile(recordNumber));
            /* Check if the date of the record obtained is within the date range and if it is , increase the count by 1
             * and total up the fee of that service record. Do this for every service record under the provider's name */
            if (newRecord.getServiceDate().after(startDate) && newRecord.getServiceDate().before(endDate)) {
                ++count;
                totalFees += newRecord.getFee();
            }//end if
        }//end for
        //return a provider payment object.
        return new ProviderPayment(name, id, count, totalFees);
    }//end writeProviderReport

    /* This function uses writeProviderReport capabilities of returning a service record and add that return service record
     * to the list of payments that will be used to generate a payment summary it also write multiple reports at once */
    public static PaymentSummary writeAllReports(File directory, Date startDate, Date endDate) throws IOException {
        //create a local PaymentSummary object called summary
        PaymentSummary summary = new PaymentSummary();
        //ensure that a directory exist before creating files
        Util.ensureDirectoryExists(directory, "provider's folder");
        //go through all the providers available in chocAn regardless of their status and create the appropriate report for them
        //Fixed off-by-one --Griffin
        for (int i = lowestProviderNum; i <= getMaxProviderNumber(); i++) {
            //use the findByID function to get the customer object with the unique ID passed in as argument
            Optional<Customer> foundCustomer = Customer.findByID(i);
            //if such a customer exists
            if (foundCustomer.isPresent()) {
                try {
                    //cast down customer to a provider
                    Provider foundProvider = (Provider) foundCustomer.get();
                    //create a report file for that provider using the provider's unique ID as the name of the file
                    File target = new File(directory, i + ".txt");
                    if(!target.createNewFile()){
                        System.out.println("File " + i + ".txt couldn't be created");
                    }
                    //create a new local print stream object called targetStream
                    PrintStream targetStream = new PrintStream(target);
                    //add the ProviderPayment obtained from writing a single report to the PaymentSummary list
                    summary.addPayment(foundProvider.writeProviderReport(targetStream, startDate, endDate));
                } catch (ClassCastException ex) {
                    //in the case of a class cast exception, we display to the user that the ID is not a provider
                    throw new RuntimeException("The Customer id " + i + " is not a provider!", ex);
                }//end try catch block
            }//end if
        }//end for
        //return the address of that Payment Summary object
        return summary;
    }//end writeAllReports
}//end Provider class