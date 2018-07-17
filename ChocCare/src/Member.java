//Fadi Labib
//CS300 - Fall 2016
//ChocAn Software
//Started: 11/12/2016 12:00 AM
//Ended: 11/16/2016 10:26 AM

import java.io.*;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;

/* Member is a derived class from the abstract base class "Customer". It has the same functionality of the customer class. There is a txt file
titled "maxMemberNumber.txt" and it contains a single number which is the last number used for a member. */
public class Member extends Customer {
    static final int lowestMemberNum = 100000000;

    /* Creates a member and assign them an automatic ID and checks if their Status is a valid member status */
    public Member(String name, String address, String city,String state, String zip, Status status) throws IOException {
        super(name, reserveNextMemberNumber(), address, city, state, zip, status);
        if(!status.isMember()){
            throw new RuntimeException("Member Status is NOT_VALID");
        }//end if
    }//end constructor

    /* Create a member from file */
    public Member(File target) throws IOException {
        super(target);
    }//end file constructor

    /* Checks if a member status assigned is valid and return true or false accordingly */
    @Override
    public boolean isValidStatus(Status status) {
        return status.isMember()&& super.isValidStatus(status);
    }

    /* Gets the last used member number from a text file and returns it */
    static int getMaxMemberNumber() throws FileNotFoundException {
        Scanner input; //A scanner input that starts as null
        int maxMemberNumber; //local variable to store the current maxMemberNumber
        try{
            //open the maxMemberNumber.txt
            input = new Scanner(new File("maxMemberNumber.txt"));
            maxMemberNumber = input.nextInt();
            input.close();
        //catch error in the case the file is not found and create the file
        }catch(FileNotFoundException e){
            //There is no file and thus this is the first member in existence and they have the value lowestMemberNum -1
            maxMemberNumber = lowestMemberNum - 1;
        }//end catch block

        //return the value stored in file which is the maxMemberNumber that has last been used
        return maxMemberNumber;
    }//end getMaxMemberNumber

    /* Finds the next member number to be used when a new member is added */
    static int reserveNextMemberNumber() throws IOException {
        int currentMaxNum = getMaxMemberNumber(); //temp value that stores last used member number
        ++currentMaxNum; //increment that number by 1

        //write the new number to the file, which will be the current max number (replaces the current max number in file)
        PrintWriter write;
        write = new PrintWriter("maxMemberNumber.txt");
        write.println(currentMaxNum);
        write.close();

        //return the new increment value and this is the current member number to be used.
        return currentMaxNum;
    }//end reserveNextMemberNumber

    /* Call Customer write report and adds a line to the top of it indicating that it's a member report*/
    @Override
    public void writeReport(PrintStream target, Date startDate, Date endDate) throws IOException {
        target.println("====Member Report====");
        super.writeReport(target, startDate, endDate);
    }//end writeReport

    /* Creates multiple reports for all members that are part of the ChocAn System */
    public static void writeAllReports(File directory, Date startDate, Date endDate) throws IOException {
        //ensure that a directory exist before creating files
        Util.ensureDirectoryExists(directory, "member's folder");
        //go through all available members */
        for(int i = lowestMemberNum; i < getMaxMemberNumber(); i++) {
            //load the members data based on their id
            Optional<Customer> foundCustomer = Customer.findByID(i);
            //if member is present we write a report for it
            if (foundCustomer.isPresent()) {
                try {
                    //cast down the customer to a member
                    Member foundMember = (Member) foundCustomer.get();
                    /* Create a file target that will be saved into a directory passed in as argument.
                    * File name is based on the member id number */
                    File target = new File(directory, i + ".txt");
                    if(!target.createNewFile()){
                        System.out.println("File " + i + ".txt couldn't be created");
                    }
                    /* Create a PrintStream Object called targetStream */
                    PrintStream targetStream = new PrintStream(target);
                    /* Call the writeReport function passing it in that stream along with the start and end date */
                    foundMember.writeReport(targetStream, startDate, endDate);
                } catch (ClassCastException ex) {
                    throw new RuntimeException("The Customer id " + i + " is not a member!", ex);
                }//end try catch block
            }//end if
        }//end for loop
    }//end writeAllReports
}//end customer object