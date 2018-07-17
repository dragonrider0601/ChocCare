//Fadi Labib
//CS300 - Fall 2016
//ChocAn Software
//Started: 11/18/2016 6:00 PM
//Ended: 11/27/2016 10:12 AM

import java.io.IOException;
import java.util.Optional;

/* OperatorMenu is a derived class from the abstract base class “Menu and it is also the super class of “Manager”. ChocAn operators run
OperatorMenu. It has functions for creating and modifying Providers and Members.  */
public class OperatorMenu extends Menu {

    @Override
    void mainMenu() {
        int isDone = 0; //allows the user to stop choosing options

        while (isDone == 0) {
            //display to the user all the different functionalities
            System.out.println("\nWelcome Operator, Please Choose one of the following options: ");
            System.out.println("\nl - Logout");
            System.out.println("a - Add Customers");
            System.out.println("u - Update Customer");

            //use switch statement to decide which one to pick based in user's input
            switch (getUserInput()) {
                case 'l': {
                    System.out.println("Goodbye.");
                    isDone = 1;
                    break; //exit
                }//end case 'l'
                case 'a': {
                    addCustomer(isAddMember()); //adds a customer
                    isDone = 0;
                    break;
                }//end case 'a'
                case 'u': {
                    customerToUpdate().ifPresent(this::updateCustomerData);//updates customer data accordingly
                    isDone = 0;
                    break;
                }//end case 'u'
                //any other option is invalid
                default: {
                    System.out.println("Invalid input!");
                    isDone = 0;
                    break;
                }//end default
            }//end switch statements
        }//end while
    }//end mainMenu

    /* This method evaluate the value of isMember and if it's true it calls adding member method, and if false calls adding provider method */
    private void addCustomer(boolean isMember) {
        if (isMember) {
            addMember();
        } else {
            addProvider();
        }//end else
    }//end add customer

    /* This helper method specifically takes care of prompting the user to make a decision whether they want to add a member or a provider.
     * based on the decision a boolean value will be returned and it will be used by the addCustomer method to figure which type of customer
      * to add*/
    private boolean isAddMember(){
        char customerType; //local variable for customer type
        boolean isMember = false; //flag starts as false
        do {
            System.out.println("Would you like to add a Provider or a Member? ");
            customerType = promptChar("For Provider enter 'p' and for Member enter 'm'");
            if (customerType == 'p') {
                isMember = false; //customer choose a provider, isMember = false;
            } else if (customerType == 'm') {
                isMember = true; //customer choose a member, isMember = true;
            }//end if-else if
        }while (customerType != 'p'&& customerType != 'm');
        return isMember; //return the flag indicating the type of customer to be added
    }//end is AddMember

    /* This method specifically adds new Member to the system by prompting the operator for all the personal information need to create
     * a file for a member */
    private void addMember(){
        String name = promptString("Enter the new Member's name: ");
        String address = promptString("Enter the street address (DO NOT include city, state or zip): ");
        String city = promptString("Enter the city name: ");
        String state = promptString("Enter the state name: ");
        String zip = promptString("Enter the zip-code (NNNNN): ");
        char memberStatus;
        Customer.Status status = null;
        int id; //holds the id of the member to display it to the user
        do {
            memberStatus = promptChar("Enter a Status 'p' (for paid) or 'e' for(expired): ");
            if (memberStatus == 'p') {
                status = Customer.Status.PAID; //set status to Paid
            } else if (memberStatus == 'e') {
                status = Customer.Status.EXPIRED; //set status to Expired
            }//end if-else if
        } while (status == null); //the user input is not a "p" or "e" so status stays null.

        /* At this point all the information need to create a member file is there, so we try and do that */
        try {
            Member newMember = new Member(name, address, city, state, zip, status);
            newMember.saveToFile(newMember.getDefaultFile()); //data is valid, ID has been assigned, save the Member to an XML file
            id = newMember.getID();
            System.out.println("\nMember added successfully!");
            System.out.println("Member ID: " + id);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to assign an ID for the member!", ex);
        }//end try catch block
    }//end addMember

    /* This method specifically adds new Provider to the system by prompting the operator for all the personal information need to create
     * a file for a provider */
    private void addProvider(){
        String name = promptString("Enter the new Provider's name: ");
        String address = promptString("Enter the street address (DO NOT include city, state or zip): ");
        String city = promptString("Enter the city name: ");
        String state = promptString("Enter the state name: ");
        String zip = promptString("Enter the zip-code (DDDDD): ");
        char providerStatus;
        Customer.Status status = null;
        int id; //holds the id of the member to display it to the user
        do {
            providerStatus = promptChar("Enter a Status i (for in network) or o for(out of network): ");
            if (providerStatus == 'i') {
                status = Customer.Status.IN_NETWORK; //set status to In Network
            } else if (providerStatus == 'o') {
                status = Customer.Status.OUT_OF_NETWORK; //set status to Out Of Network
            }//end if-else if
        } while (status == null); //the user input is not a "i" or "o" so status stays null.

        /* At this point all the information need to create a provider file is there, so we try and do that */
        try {
            Provider newProvider = new Provider(name, address, city, state, zip, status);
            newProvider.saveToFile(newProvider.getDefaultFile());
            id = newProvider.getID();
            System.out.println("\nProvider added successfully!");
            System.out.println("Provider ID: " + id);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to assign an ID for the provider!", ex);
        }//end try catch block
    }//end addProvider

    /* This method is in charge of updating a single customer passed in as argument and it will update any and all their information as
     * many times as the operator desires, by choosing the c - cancel option or choosing n - no when prompted if they want to update another
      * field, that customer will be saved with all the new updates the operator changed */
    private void updateCustomerData(Customer customer) {
        char again = 'n'; //takes user input of y or n when prompted if they want to update another field
        boolean isCancel; //flag that returns the user to operator menu options

        do {
            System.out.println("What field would you like to update: ");
            System.out.println("c - Cancel");
            System.out.println("n - Update Name");
            System.out.println("a - Update Address");
            System.out.println("s - Update Status");
            switch (getUserInput()) {
                case 'c': {
                    isCancel = true; //cancel the updating
                    break; //exit and go to saving method
                }//end case 'c'
                case 'n': {
                    char cancel = promptChar("type c for cancel or any letter to continue: ");
                    if(cancel == 'c'){
                        isCancel = true;
                    }else{
                        String newName = promptString("Enter the new name: ");
                        customer.setName(newName); //update the name
                        isCancel = false;
                    }//end if-else
                    break;
                }//end case 'n'
                case 'a': {
                    char cancel = promptChar("type c for cancel or any letter to continue: ");
                    if(cancel == 'c'){
                        isCancel = true;
                    }else {
                        String newAddress = promptString("Enter the street address (DO NOT include city, state or zip): ");
                        String newCity = promptString("Enter the city name: ");
                        String newState = promptString("Enter the state name: ");
                        String newZip = promptString("Enter the zip-code (DDDDD): ");
                        customer.setAddress(newAddress, newCity, newState, newZip); //update address information
                        isCancel = false;
                    }//end if-else
                    break;
                }//end case 'a'
                case 's': {
                    Customer.Status currentStatus = customer.getStatus(); //get the current status
                    System.out.println("Current Status is: " + currentStatus.toString()); //display it to the user
                    Customer.Status tempStatus; //local variable to store the updated status
                    do{
                        tempStatus = getCustomerStatusInput(); //get customer input (if same as displayed status, then no changes are made)
                        if(!customer.isValidStatus(tempStatus)){
                            System.out.println("\nYou entered an invalid status for the type of customer you are trying to update!");
                        }
                    }while(!customer.isValidStatus(tempStatus)); //if status is not valid for the type of customer, prompt again for valid status
                    isCancel = false;
                    break;
                }//end case 's'
                default: {
                    System.out.println("Invalid input!");
                    isCancel = false;
                    break;
                }//end default
            }//end switch
            if(!isCancel){
                again = promptChar("Would you like to update any other field (y - yes): ");
            }//end if
        } while (again == 'y'); //the user has done all the updates for that one customer
        customer.saveToFile(customer.getDefaultFile()); //save the new updates
    }//end update customer data

    /* This is a helper method that uses the customer ID input to decide what to do. if there is a customer to return, return it; otherwise
     * return the empty option and deal with the ramification when it comes time to do the actual update on the customer */
    private Optional<Customer> customerToUpdate(){
        Optional<Customer> foundCustomer;
        do {
            int id = getCustomerIdInput();
            if(id == -1){
                return Optional.empty();
            }//if id is -1 its equivalent to canceling the updating
            foundCustomer = Customer.findByID(id); //use id to get the right customer or the empty option depending if they exist or not
        }while(!foundCustomer.isPresent()); //end do-while
        return foundCustomer; //return whatever customer was found
    }//end customer update

    /* This method simply prompts the user for what type of status they would like to assign only four options are valid; user can't quit
     * this method until they input one of those four options */
    private Customer.Status getCustomerStatusInput(){
        char newStatus; //local variable to store users status input
        Customer.Status statusSet = null; //status start at null

        do{
            System.out.println("Here are your status option, any other is invalid!");
            System.out.println("i - In Network");
            System.out.println("o - out of Network");
            System.out.println("p - Paid");
            System.out.println("e - Expired");
            newStatus = promptChar("Enter a Status (i, o, p, or e): ");

            switch(newStatus){
                case 'i':{
                    statusSet = Customer.Status.IN_NETWORK;
                    break;
                }//end case 'i'
                case 'o': {
                    statusSet = Customer.Status.OUT_OF_NETWORK;
                    break;
                }//end case 'o'
                case 'p': {
                    statusSet = Customer.Status.PAID;
                    break;
                }//end case 'pi'
                case 'e': {
                    statusSet = Customer.Status.EXPIRED;
                    break;
                }//end case 'e'
                default:
                    System.out.println("This is not a valid status");
            }//end switch
        }while(statusSet == null); //end do while

        return statusSet; //return correct status
    }//end updateStatus function

    /* return user character input for menu options */
    private char getUserInput(){
        //ask the user for an input
        return promptChar("\n\nWhich letter do you pick: ");
    }//end getUserInput

    /* return user id input for parts of the software that requires an id input (customer id) */
    private int getCustomerIdInput(){
            return promptInt("\nEnter the id number for the customer you wish to update (-1 to cancel): ");
    }//end getCustomerInput
}//end class operatorMenu