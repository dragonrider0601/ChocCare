import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *Created by Brandon Le.
 *The ServiceRecord class holds information about a service a member
 * receives from a provider. Each time a provider serves a member,
 * the BillingMenu creates a ServiceRecord object for the visit.
 * The BillingMenu creates the object with data the class passes
 * into the object. The object takes input data from BillingMenu
 * or from a file, which holds stored data from the service record
 * file. The class is able to write data out to a file, namely to
 * the database. The Customer class can construct a ServiceRecord
 * object from a file for reports creation. ServiceRecord bills
 * ChocAn and is part of several reports.
 */
public class ServiceRecord {
    private Date currentDateTime; //current date and time (MM-DD-YYYY HH:MM:SS)
    private Date serviceDate; //Date of service was provided (MM-DD-YYYY)
    private int providerNumber; //Provider number
    private int memberNumber; //Member number
    private int serviceCode; //Service code
    private int fee; //Fee represented in cents
    private BigInteger recordNumber; //Service record number
    private String comment; //Optional comment of up to 100 characters

    /*Constructor using data passed in from BillingMenu class */
    public ServiceRecord(Date serviceDate, int providerNum, int memberNum, int serviceCode, int fee, String comment){
            this.currentDateTime = new Date();
            this.serviceDate = serviceDate;
            this.providerNumber = providerNum;
            this.memberNumber = memberNum;
            this.serviceCode = serviceCode;
            this.fee = fee;
            this.recordNumber = calculateRecordNumber();
            this.comment = comment;
    }

    /*Get the data field from the xml file.*/
    private String getField(String name, Document xmlDocument) {
        NodeList list = xmlDocument.getElementsByTagName(name);
        if (list.getLength() != 1) {
            throw new RuntimeException("XML has invalid field " + name + " (" + list.getLength() + " nodes found).");
        }

        return list.item(0).getTextContent();
    }

    /*Constructor that reads data members from the xml file the ServiceRecord was
    * written to*/
    protected ServiceRecord(File source){
        this(Util.openStreamNoThrow(source));
    }

    protected ServiceRecord(InputStream source) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document xml = documentBuilder.parse(source);

            String inputCurrentDateTime = getField("currentDateTime", xml);
            currentDateTime = new Date(Long.parseLong(inputCurrentDateTime));

            String inputServiceDate = getField("serviceDate", xml);
            serviceDate = new Date(Long.parseLong(inputServiceDate));

            providerNumber = Integer.parseInt(getField("providerNumber", xml)); //Provider number
            memberNumber = Integer.parseInt(getField("memberNumber", xml)); //Member number
            serviceCode = Integer.parseInt(getField("serviceCode", xml)); //Service code
            fee = Integer.parseInt(getField("fee", xml)); //Fee represented in cents
            recordNumber = new BigInteger(getField("recordNumber", xml)); //Service record number
            comment = getField("comment", xml); //Optional comment of up to 100 characters
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Could not read XML" + source, e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number in XML " + source, e);
        }
    }

    /*Returns the record number for a service record, calculated from the provider number and current time. */
    private BigInteger calculateRecordNumber(){
        return BigInteger.valueOf(providerNumber).shiftLeft(64).add(BigInteger.valueOf(currentDateTime.getTime()));
    }

    /*Print the field to the xml file*/
    private void printField(String indent, String name, String value, PrintStream target) {
        target.println(indent + "<" + name + ">" + value + "</" + name + ">");
    }

    /*Write data out to a xml file the ServerConnection determines. Exception handling deals with input issues. */
    protected void writeService(PrintStream out){
            out.println("<serviceRecords>");
            printField(" ", "currentDateTime", String.valueOf(currentDateTime.getTime()), out);
            printField("  ", "serviceDate", String.valueOf(serviceDate.getTime()), out);
            printField("  ", "providerNumber", String.valueOf(providerNumber), out);
            printField("  ", "memberNumber", String.valueOf(memberNumber), out);
            printField("  ", "serviceCode", String.valueOf(serviceCode), out);
            printField("  ", "fee", String.valueOf(fee), out);
            printField("  ", "recordNumber", String.valueOf(recordNumber), out);
            printField(" ", "comment", comment, out);
            out.println("</serviceRecords>");
    }

    /*Method to save a ServiceRecord to an xml file. Method uses writeService method. */
    public void saveServiceToFile(File target) {
        try {
            if (!target.exists())
                target.createNewFile();
            //TODO ensure this properly overwrites (not append)
            PrintStream out = new PrintStream(target);
            writeService(out);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Error saving customer " + e);
        }
    }

    /*Display the ServiceRecord on the screen using the PrintStream
    target argument that this function takes. */
    public void displayRecord(PrintStream target){
        SimpleDateFormat currentft = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        SimpleDateFormat serviceft = new SimpleDateFormat("MM-dd-yyyy");

        /*
        Construct the string to be outputted. Change currentDateTime and
        serviceDate to the correct format.
        */
        //TODO Write the names of these fields before writing them! The user has no idea what data all these numbers correspond to
        //TODO Include a header indicating that you are a service record; include the record number here (and not below)
        String record = "Service record number: " + recordNumber + "\n\t" +
                "Current date and time: " + currentft.format(currentDateTime) /*currentDateTime.toString()*/ + "\n\t" +
                "Service date and time: " + serviceft.format(serviceDate) /*serviceDate.toString()*/ + "\n\t" +
                "Provider number: " + providerNumber + "\n\t" +
                "Member number: " + memberNumber + "\n\t" +
                "Service code: " + serviceCode + "\n\t" +
                "Service fee: " + fee + "\n\t" +
                "Service comment: " + comment + "\n";

        //Modified by Griffin
        //PrintStream toFile = new PrintStream(target); //Removed (redundant/pointless)
        //PrintStream screen = System.out; //Removed (not needed, also redundant (just use System.out directly, except you shouldn't do that)) /* No need for new PrintStream(System.out) because System.out is a PrintStream*/
        //writeService(toFile); Removed (we don't want to write XML)
        target.print(record); //Changed to target
    }

    /*Returns the record number */
    public BigInteger getRecordNumber(){
        return recordNumber;
    }

    /*Returns the provider number */
    public int getProviderNumber(){
        return providerNumber;
    }

    /*Returns the member number */
    public int getMemberNumber(){
        return memberNumber;
    }

    /*Returns the fee */
    public int getFee(){
        return fee;
    }

    /*Returns the service date */
    public Date getServiceDate(){
        return serviceDate;
    }

    //METHODS FOR TESTING
    /*Returns the current date and time*/
    public Date getCurrentDateTime() {
        return currentDateTime;
    }

    /*Returns service code*/
    public int getServiceCode() {
        return serviceCode;
    }

    /*Returns comment*/
    public String getComment()
    {
        return comment;
    }

    /*Main used only for preliminary testing*/
    public static void main(String[] args) throws FileNotFoundException, ParseException {
        /*File input = new File("/home/yua/Desktop/testServiceRecord.xml");
        long testSetDate = new Long("500000000000");
        Date testSD = new Date(testSetDate); //Date of service was provided (MM-DD-YYYY)
        int testPN = 10000000; //Provider number
        //NO LEADING 0's or 0x otherwise interpretted as octal and hex
        int testMN = 3000000; //Member number
        int testSC = 4000; //Service code
        int testF = 10000000; //Fee represented in cents
        String comment = "This is a test"; //Optional comment of up to 100 characters
*/
        /*
        ServiceRecord testWrite = new ServiceRecord(testSD, testPN, testMN, testSC, testF, comment);
        testWrite.saveServiceToFile(input);

        PrintStream inputStream = new PrintStream(input);
        testWrite.displayRecord(inputStream);

        ServiceRecord testInput = new ServiceRecord(input);
        testInput.displayRecord(inputStream);

        String out = testInput.getServiceDate().toString() + "\n\t" +
                        testInput.getProviderNumber() + "\n\t" +
                        testInput.getMemberNumber() + "\n\t" +
                        testInput.getServiceCode() + "\n\t" +
                        testInput.getFee() + "\n\t" +
                        testInput.getRecordNumber().toString() + "\n\t" +
                        testInput.getComment() + "\n";

        System.out.println(out);
        */
    //DIFFERENT SECTION OF TESTING
        File input1 = new File("testServiceRecord1.xml");
        File input2 = new File("testServiceRecord2.xml");
        File input3 = new File("testServiceRecord3.xml");
        File input4 = new File("testServiceRecord4.xml");

        Date[] testDate = {
            new Date(0),
            new Date(10000000),
            new Date(1000000000),
            new Date(2000000000)
        };

        //9-digit
        int[] testProviderNumber = {
            1000,
            10000,
            100000,
            9999999
        };

        //9-digit
        int[] testMemberNumber = {
            100000000,
            200000000,
            300000000,
            999999999
        };

        //6-digit
        int[] testServiceCode = {
            0,
            100000,
            123456,
            999999
        };

        int[] testFee = {
            0,
            10000,
            100000000,
            999999999
        };


        String[] testComment = {
            "This works!",
            "How do you add this?",
            "THE ANSWER WORKS IN EVERY CASE I THINK!",
            "Well done, good job."
        };

        ServiceRecord [] testServiceRecord = {
            new ServiceRecord(testDate[0], testProviderNumber[0],
                    testMemberNumber[0], testServiceCode[0], testFee[0], testComment[0]),
                new ServiceRecord(testDate[1], testProviderNumber[1],
                    testMemberNumber[1], testServiceCode[1], testFee[1], testComment[1]),
                new ServiceRecord(testDate[2], testProviderNumber[2],
                    testMemberNumber[2], testServiceCode[2], testFee[2], testComment[2]),
                new ServiceRecord(testDate[3], testProviderNumber[3],
                    testMemberNumber[3], testServiceCode[3], testFee[3], testComment[3])
        };

        String reference = "Wed Dec 31 16:00:00 PST 1969\n\t" +
                "1000\n\t" +
                "100000000\n\t" +
                "0\n\t" +
                "0\n\t" +
                //"18446744075189695563210\n\t" +
                "This works!\n" +

                "Wed Dec 31 18:46:40 PST 1969\n\t"+
                "10000\n\t" +
                "200000000\n\t" +
                "100000\n\t" +
                "10000\n\t" +
                //"184467440738575660107212\n\t" +
                "How do you add this?\n"+

                "Mon Jan 12 05:46:40 PST 1970\n\t" +
                "100000\n\t" +
                "300000000\n\t" +
                "123456\n\t" +
                "100000000\n\t" +
                //"1844674407372435305547212\n\t" +
                "THE ANSWER WORKS IN EVERY CASE I THINK!\n" +

                "Fri Jan 23 19:33:20 PST 1970\n\t" +
                "9999999\n\t" +
                "999999999\n\t" +
                "999999\n\t" +
                "999999999\n\t" +
                //"184467422290352922594773347\n\t" +
                "Well done, good job.\n";

        testServiceRecord[0].saveServiceToFile(input1);
        testServiceRecord[1].saveServiceToFile(input2);
        testServiceRecord[2].saveServiceToFile(input3);
        testServiceRecord[3].saveServiceToFile(input4);

        int numServiceRecords = testServiceRecord.length;

        String out = "";
        for(int i = 0; i < numServiceRecords; ++i)
                out += //testServiceRecord[i].getCurrentDateTime().toString() + "\n\t" +
                        testServiceRecord[i].getServiceDate().toString() + "\n\t" +
                        testServiceRecord[i].getProviderNumber() + "\n\t" +
                        testServiceRecord[i].getMemberNumber() + "\n\t" +
                        testServiceRecord[i].getServiceCode() + "\n\t" +
                        testServiceRecord[i].getFee() + "\n\t" +
                        //testServiceRecord[i].getRecordNumber().toString() + "\n\t" +
                        testServiceRecord[i].getComment() + "\n";


        System.out.println(out);

        ServiceRecord[] fromFile = {
                new ServiceRecord(input1),
                new ServiceRecord(input2),
                new ServiceRecord(input3),
                new ServiceRecord(input4)
        };

        int numFromFileServiceRecords = fromFile.length;
        String outFromFile = "";
        for(int i = 0; i < numFromFileServiceRecords; ++i)
                outFromFile += //testServiceRecord[i].getCurrentDateTime().toString() + "\n\t" +
                        fromFile[i].getServiceDate().toString() + "\n\t" +
                        fromFile[i].getProviderNumber() + "\n\t" +
                        fromFile[i].getMemberNumber() + "\n\t" +
                        fromFile[i].getServiceCode() + "\n\t" +
                        fromFile[i].getFee() + "\n\t" +
                        //testServiceRecord[i].getRecordNumber().toString() + "\n\t" +
                        fromFile[i].getComment() + "\n";

        System.out.println("\n\nFROM FILE\n" + outFromFile);

        for (ServiceRecord aFromFile : fromFile) {
            aFromFile.displayRecord(System.out);
        }

        System.out.println(outFromFile.compareTo(reference));

        System.out.println(fromFile[0].getRecordNumber().shiftRight(32) + "\n" +
            fromFile[1].getRecordNumber().shiftRight(32) + "\n" +
            fromFile[2].getRecordNumber().shiftRight(32) + "\n" +
            fromFile[3].getRecordNumber().shiftRight(32) + "\n");
    }
}
