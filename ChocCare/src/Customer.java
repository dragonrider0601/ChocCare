import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gstone on 11/10/2016.
 */
public abstract class Customer {

    private static final int LOWEST_MEMBER_NUM = 100000000;
    private static final DateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    protected String name;
    protected int id; //ID number; 9 digits; starts with 0 for providers, starts with 1 for members
    private String address;
    private String state;
    private String city;
    private String zip;
    protected Status status; //Status indicating whether the customer is active (in-network/paying)
    protected final List<BigInteger> serviceRecords;

    /* All-data constructor (to be called by subclass constructors, after they select an ID) */
    protected Customer(String name, int id, String address, String state, String city, String zip, Status status) {
        if (Arrays.stream(new Object[]{name, address, state, city, zip, status}).anyMatch(o -> o == null)) {
            throw new IllegalArgumentException("Arguments cannot be null. Received " + Arrays.toString(new Object[]{name, id, address, state, city, zip, status}));
        }
        this.name = name;
        this.id = id;
        this.address = address;
        this.state = state;
        this.city = city;
        this.zip = zip;
        this.status = status;
        serviceRecords = new ArrayList<>();
    }

    private String getField(String name, Document xmlDocument) {
        NodeList list = xmlDocument.getElementsByTagName(name);
        if (list.getLength() != 1) {
            throw new RuntimeException("XML has invalid field " + name + " (" + list.getLength() + " nodes found).");
        }
        return list.item(0).getTextContent();
    }

    /* Reads the XML written by saveToFile to construct a Customer */
    protected Customer(File source) {
        this(Util.openStreamNoThrow(source));
    }
    /* Reads the XML written by writeXML to construct a Customer */
    protected Customer(InputStream source) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document xml = documentBuilder.parse(source);
            name = getField("name", xml);
            id = Integer.parseInt(getField("id", xml));
            address = getField("address", xml);
            state = getField("state", xml);
            city = getField("city", xml);
            zip = getField("zip", xml);
            status = Status.valueOf(getField("status", xml));
            serviceRecords = new ArrayList<>();
            NodeList recordNodes = xml.getElementsByTagName("serviceRecord");
            for (int i = 0; i < recordNodes.getLength(); i++) {
                serviceRecords.add(new BigInteger(recordNodes.item(i).getTextContent()));
            }
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new RuntimeException("Could not read XML " + source, ex);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Invalid id or ServiceRecord in XML " + source, ex);
        }
    }


    /* Returns true if the ID corresponds to a member; false if it corresponds to a provider. */
    public static boolean isMember(int id) {
        return id >= LOWEST_MEMBER_NUM;
    }

    /* Based on the first digit of the ID, looks up the corresponding Member or Provider, loads it from file, and returns it (or the empty Optional, if none is found) */
    /* If there is an error, returns the empty optional */

    public static Optional<Customer> findByID(int id) {
        File target = getDefaultFile(id);
        if (target.exists()) {
            try {
                if (isMember(id)) {
                    return Optional.of(new Member(target));
                } else {
                    return Optional.of(new Provider(target));
                }
            } catch (IOException ex) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /* Returns the default file location for storing the customer with that ID, based on the working directory. For loading from file. */
    public static File getDefaultFile(int id) {
        String folderName = isMember(id) ? "members" : "providers";
        File folder = new File(folderName);
        Util.ensureDirectoryExists(folder, folderName + " folder");
        File target = new File(folder, id + ".xml");
        return target;
    }

    /* Adds the service record to the list of records */
    public void addServiceRecord(BigInteger recordNum) {
        serviceRecords.add(recordNum);
    }

    /* Saves via XML*/
    public void saveToFile(File target) {
        try {
            if (!target.exists()) target.createNewFile();
            //TODO ensure this properly overwrites (not append)
            PrintStream out = new PrintStream(target);
            writeXML(out);
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException("Error saving customer " + id, ex);
        }
    }

    private void printField(String indent, String name, String value, PrintStream target) {
        target.println(indent + "<" + name + ">" + value + "</" + name + ">");
    }

    //Write all the XML data to be save to files; suitable for construction with Customer(File source)
    protected void writeXML(PrintStream out) {
        out.println("<Customer>");
        printField("  ", "name", name, out);
        printField("  ", "id", String.valueOf(id), out);
        printField("  ", "address", address, out);
        printField("  ", "state", state, out);
        printField("  ", "city", city, out);
        printField("  ", "zip", zip, out);
        printField("  ", "status", status.toString(), out);
        out.println("  <serviceRecords>");
        for (BigInteger i : serviceRecords) {
            printField("    ", "serviceRecord", i.toString(), out);
        }
        out.println("  </serviceRecords>");
        out.println("</Customer>");
    }

    /* Writes the customer data to the report. Subclasses should override this method to include a header describing what sort of customer this is. */
    public void writeReport(PrintStream target, Date startDate, Date endDate) throws IOException {
        writeCustomerInfo(target);
        List<ServiceRecord> servicesThisPeriod = getServicesBetweenDates(startDate, endDate);
        target.println();
        target.println(servicesThisPeriod.size() + " services"
                + " between " + REPORT_DATE_FORMAT.format(startDate) + " and " + REPORT_DATE_FORMAT.format(endDate));
        for (ServiceRecord record : servicesThisPeriod) {
            record.displayRecord(target);
        }
    }

    //Write basic customer info (no service records) for report
    private void writeCustomerInfo(PrintStream target) {
        target.println("Report for " + name + ", ID " + id);
        target.println("    " + address);
        target.println("    " + city + ", " + state + "  " + zip);
        target.println("    Status: " + status.getName());
    }

    private List<ServiceRecord> getServicesBetweenDates(Date startDate, Date endDate) {
        List<ServiceRecord> servicesThisPeriod = new ArrayList<>();
        for (BigInteger i : serviceRecords) {
            ServiceRecord record = new ServiceRecord(Util.getServiceRecordFile(i));
            Date date = record.getServiceDate();
            if (date.after(startDate) && date.before(endDate)) {
                servicesThisPeriod.add(record);
            }
        }
        return servicesThisPeriod;
    }

    /* Returns the default file location for storing this Customer, based on the working directory and the customer ID. For saving to file. */
    public File getDefaultFile() {
        return getDefaultFile(id);
    }

    //Autogenerated equals, based on all fields. Assumes all fields are nonnull.
    //Considers anonymous classes to be the same class (for testing)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //added .isAnonymousClass() checks for easier testing
        if (o == null || (getClass() != o.getClass() && !(getClass().isAnonymousClass() && o.getClass().isAnonymousClass()))) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (!address.equals(customer.address)) return false;
        if (!city.equals(customer.city)) return false;
        if (!name.equals(customer.name)) return false;
        if (!serviceRecords.equals(customer.serviceRecords)) return false;
        if (!state.equals(customer.state)) return false;
        if (status != customer.status) return false;
        if (!zip.equals(customer.zip)) return false;

        return true;
    }

    //Autogenerated hashCode, based on all fields. Assumes all fields are nonnull.
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id;
        result = 31 * result + address.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + zip.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + serviceRecords.hashCode();
        return result;
    }

    //Autogenerated toString
    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", address='" + address + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", status=" + status +
                ", serviceRecords=" + serviceRecords +
                '}';
    }

    //Update name
    public void setName(String newName) {
        if (newName == null) throw new IllegalArgumentException("Name cannot be null.");
        this.name = newName;
    }

    //Update address
    public void setAddress(String address, String state, String city, String zip) {
        if (Arrays.stream(new Object[]{address, state, city, zip}).anyMatch(o -> o == null)) {
            throw new IllegalArgumentException("Address fields cannot be null. Received " + Arrays.toString(new Object[]{address, state, city, zip}));
        }
        this.address = address;
        this.state = state;
        this.city = city;
        this.zip = zip;
    }

    //Returns status
    public Status getStatus() {
        return status;
    }

    public int getID(){
        return id;
    }

    //Update status
    //isValidStatus(newStatus) must return true
    public void setStatus(Status newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("Status cannot be null.");
        if (!isValidStatus(newStatus)) throw new IllegalArgumentException("Invalid status " + newStatus.getName());
        status = newStatus;
    }

    //Return true if the status is valid for this type of Customer
    public boolean isValidStatus(Status status) {
        return true;
    }
    /* Customer status--indicates whether the customer is active or not */

    public static enum Status {
        PAID, EXPIRED, IN_NETWORK, OUT_OF_NETWORK;

        //returns true if this customer can bill ChocAn
        public boolean isActive() {
            return this == PAID || this == IN_NETWORK;
        }

        //returns true if the status can describe a member
        public boolean isMember() {
            return this == PAID || this == EXPIRED;
        }

        //returns true if the status can describe a provider
        public boolean isProvider() {
            return this == IN_NETWORK || this == OUT_OF_NETWORK;
        }

        //displays the name of the status (in lowercase)
        public String getName() {
            switch (this) {
                case PAID:
                    return "Paid";
                case EXPIRED:
                    return "Expired";
                case IN_NETWORK:
                    return "In Network";
                case OUT_OF_NETWORK:
                    return "Out Of Network";
                default:
                    return "INVALID STATUS";
            }
        }
    }
}
