import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by gstone on 11/17/2016.
 */
public class ServerConnection {
    private static final String providerDirectoryFilename = "services.xml"; //sam-changed to services.xml

    /* Returns the active/inactive status of a member */
    public static Optional<Customer.Status> checkStatus(int customerID) {
        return Customer.findByID(customerID).map(c -> c.getStatus());
    }

    /* Adds a ServiceRecord to the ChocAn database. Updates the appropriate Member and Provider.
    * Warns the user if the member is not present. */
    public static void submitServiceRecord(ServiceRecord record) throws IOException {
        record.saveServiceToFile(Util.getServiceRecordFile(record.getRecordNumber()));
        int[] customerIDs = {record.getMemberNumber(), record.getProviderNumber()};
        for (int id : customerIDs) {
            Optional<Customer> target = Customer.findByID(id);
            if (target.isPresent()) {
                Customer customer = target.get();
                customer.addServiceRecord(record.getRecordNumber());
                customer.saveToFile(customer.getDefaultFile());
            } else {
                System.out.println("Warning: Customer with ID " + id + " involved in service record " + record.getRecordNumber() + " not found!");
            }
        }
    }

    public static ProviderDirectory getProviderDirectory(){
        File target = new File(providerDirectoryFilename);

        if (target.exists()) {
            try {
                return new ProviderDirectory(target);
            } catch (IOException ex) {
                throw new RuntimeException("Could not load provider directory.", ex);
            }
        } else {
            //Could return empty provider directory, but without a provider directory the billing menu is useless
            throw new RuntimeException("Could not find provider directory.");

        }
    }
}
