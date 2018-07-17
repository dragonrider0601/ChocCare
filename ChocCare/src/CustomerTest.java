import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.function.Function;

import static org.junit.Assert.*;

public class CustomerTest {

    private static final int lowestMemberNum = 100000000;

    //private boolean runManualTests;

    static Customer[] testCustomers = new Customer[]{
            new Customer("TestCustomer0", lowestMemberNum, "street addr", "OR", "Portland", "97229", Customer.Status.PAID) {},
            new Customer("LargeCustomerData", Integer.MAX_VALUE, "street address that has a total of 54 characters in it", "Washington D.C.", "Winchester-on-the-Severn", "97229-1234", Customer.Status.IN_NETWORK) {},
            new Customer("BlankAddress", lowestMemberNum * 2 - 1, "", "", "", "", Customer.Status.EXPIRED) {},
            new Customer("", 0, "Blank Name", "me", "Portland", "04103", Customer.Status.OUT_OF_NETWORK) {},
            new Customer("MiddlingMember", lowestMemberNum + 500, "addr", "tX", "Houston", "12345", Customer.Status.PAID) {},
            new Customer("MiddlingProvider", 500, "Clinic", "OR", "Medford", "54321", Customer.Status.IN_NETWORK) {},
            new Customer("MemberInvalidStatus", lowestMemberNum + 5400, "addr", "tX", "Houston", "12345", Customer.Status.IN_NETWORK) {},
            new Customer("ProviderInvalidStatus", 8400, "Clinic", "OR", "Medford", "54321", Customer.Status.PAID) {},
    };

    /**
     * Tests that the results of function are equal to the expected values.
     * @param expected The array of expected values
     * @param function The function to test
     * @param ignoreNull If true, accept any value from function (other than throwing an exception) when the value in expected is null
     * @param <T> The return type of the function
     */
    private <T> void testFunction(T[] expected, Function<Customer, T> function, boolean ignoreNull) {
        assertEquals("Expected array should have the same length as the length of tested objects", expected.length, testCustomers.length);
        for (int i = 0; i < testCustomers.length; i++) {
            T result = function.apply(testCustomers[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testCustomers[i].name, expected[i], result);
        }
    }

    @Before
    public void setUp() throws Exception {
        System.out.println(System.in);
        //char choice = Menu.promptChar("Run manual tests (y/N)?");
        //TODO
        //runManualTests = choice == 'y';
    }

    @Test
    public void testIsMember() throws Exception {
        Boolean[] expectedResults = {
                true,
                null,
                true,
                false,
                true,
                false,
                true,
                false
        };
        testFunction(expectedResults, (Customer c) -> Customer.isMember(c.id), true);

    }

    @Test
    public void testFindByID() throws Exception {

    }

    @Test
    public void testGetDefaultFile() throws Exception {

    }

    @Test
    public void testAddServiceRecord() throws Exception {

    }

    //Test that after writing XML, the read customer is the same as the written one
    @Test
    public void testWriteReadXML() throws Exception {
        //expected results are the input customers back
        Customer[] expectedResults = testCustomers;
        testFunction(expectedResults, (Customer c) -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream target = new PrintStream(outputStream);
            c.writeXML(target);
            target.close();
            return new Customer(new ByteArrayInputStream(outputStream.toByteArray())){};
        }, true);
    }

    @Test
    public void testSaveToFile() throws Exception {

    }

    @Test
    public void testWriteReport() throws Exception {
        Date start = new Date(1000);
        Date end = new Date(9000);
        //Ensure that no errors are thrown
        //TODO compare the results to known values
        for (Customer c : testCustomers) {
            c.writeReport(new PrintStream(new ByteArrayOutputStream()), start, end);
        }
    }

    @Test
    public void testGetDefaultFile1() throws Exception {
    }
    /*Not bothering to test setName and setAddress */

    @Test
    public void testGetStatus() throws Exception {
        Customer.Status[] expectedResults = {
                Customer.Status.PAID,
                Customer.Status.IN_NETWORK,
                Customer.Status.EXPIRED,
                Customer.Status.OUT_OF_NETWORK,
                Customer.Status.PAID,
                Customer.Status.IN_NETWORK,
                Customer.Status.IN_NETWORK,
                Customer.Status.PAID,
        };
        testFunction(expectedResults, Customer::getStatus, false);
    }

    @Test
    public void testSetStatus() throws Exception {
        Customer.Status[] statuses = Customer.Status.values();
        Customer customer = new Customer("TestSetStatus", lowestMemberNum, "street addr", "OR", "Portland", "97229", statuses[0]) {};
        assertEquals("Initial status", statuses[0], customer.getStatus());
        for (int i = statuses.length - 1; i >= 0; i--) {
            customer.setStatus(statuses[i]);
            assertEquals("Status" + i, statuses[i], customer.getStatus());
        }

    }
}