import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 *Created by yua on 11/10/16.
 *The PaymentSummary class works with the Provider and ManagerMenu class. The
 *Provider class creates a PaymentSummary object and returns the object to the
 *ManagerMenu afterwards. The application uses this class to generate EFT data
 * to a file and display the summary report to the terminal. The data for EFT
 * data and the summary report are the same. Data for the PaymentSummary class
 * is a linked list of ProviderPayment objects. These objects get data from
 * each provider. Each time the Provider class creates a ProviderPayment
 * object and fills it with data, Provider.writeAllReports adds the object
 * to the list of Provider payments using addPayment method. After the list
 * is complete, all providers have been added, PaymentSummary writes EFT
 * data to a file and the summary report is displayed onto the terminal.
 */
public class PaymentSummary {
    /*A linked list of ProviderPayment, each of which is
     payment data for the provider */
    private List<ProviderPayment> payments;

    /*Constructor for the PaymentSummary class.*/
    public PaymentSummary(){
        this.payments = new LinkedList<>();
    }

    /*Method for adding a ProviderPayment object to the payments list.*/
    public void addPayment(ProviderPayment toAdd) {
        payments.add(toAdd);
    }

    /*Method for displaying summary report and writing EFT data to
    file which is a plain text file. To display summary report,
    call this method with System.out as the argument.*/
    public void writeReport(PrintStream target){
        for(ProviderPayment payment : payments) { //Changed to for-in loop --Griffin
            payment.writeProviderNameNum(target);
            //payments.get(i).displayProviderPayment();
            //payments.get(i).writeProviderNameNum(System.out); //COMMENTED OUT BECAUSE SOMEONE WILL JUST CALL FUNCTION AGAIN WITH System.out
        }

        //PERSON WHO OPENS STREAM CLOSES IT!!!
    }

    //Methods for testing MAKE A CLASS FOR TESTING LIKE GRIFFIN DID FOR CustomerTest
    public List<ProviderPayment> getPayments() {
        return payments;
    }

    public static void main(String[] args) throws FileNotFoundException {
        PaymentSummary paysum = new PaymentSummary();
        ProviderPayment billy = new ProviderPayment("Billy", 123456789, 2, 20000);
        ProviderPayment brian = new ProviderPayment("Brian", 222222222, 5, 5000);
        ProviderPayment jane = new ProviderPayment("Jane", 333344445, 90, 30000000);
        PrintStream fileout = new PrintStream(new FileOutputStream("/home/yua/Desktop/report.txt", true));

        //paysum.payments.add(billy);
        paysum.addPayment(billy);
        paysum.addPayment(brian);
        paysum.addPayment(jane);
        for(int i = 0; i < 3; ++i)
            paysum.payments.get(i).displayProviderPayment();

        paysum.writeReport(fileout);
        paysum.writeReport(System.out);
    }
}
