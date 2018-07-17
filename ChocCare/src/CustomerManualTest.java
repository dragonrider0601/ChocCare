import java.io.IOException;
import java.util.Date;

/**
 * Created by gstone on 11/22/2016.
 */
public class CustomerManualTest {

    private static Customer[] testCustomers = CustomerTest.testCustomers;

    public static void main(String[] args) throws IOException {
        writeReports();
    }

    private static void writeReports() throws IOException {
        Date start = new Date(Menu.promptLong("Enter the start time"));
        Date end = new Date(Menu.promptLong("Enter the end time"));
        for (Customer c : testCustomers) {
            c.writeReport(System.out, start, end);
            System.out.println();
        }
    }
}
