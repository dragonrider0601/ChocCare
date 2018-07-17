import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by randyrollofson on 11/13/16.
 */
public class BillingMenuTest {
    BillingMenu billingMenu = new BillingMenu(123456789); //Added argument --Griffin
    private int testMemberNumber = 12345678; //9-digit number, 1st digit = 0
    private int maxMemberNumber = 99999999;
    private int memberNumberLength = 8; //member numbers have 9-digits but the 1st digit is 0, hence length 8
    private int testProviderNumber = 123456789;
    private String testComment = "Testing comments";


/*
    private <T> void testFunction(T[] expected, Function<Customer, T> function, boolean ignoreNull) {
        assertEquals("Expected array should have the same length as the length of tested objects", expected.length, testCustomers.length);
        for (int i = 0; i < testCustomers.length; i++) {
            T result = function.apply(testCustomers[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testCustomers[i].name, expected[i], result);
        }
*/

    //tests provider number login
    @Test
    public void testProviderLogin() throws Exception {
        assertEquals(testProviderNumber, billingMenu.getProviderNumber());
    }


    @Test
    public void testMainMenu() throws Exception {

    }

    //tests that member number is of length 8 (9-digit - leading 0)
    @Test
    public void testMemberNumberLength() throws Exception {
        int memLength = (int)(Math.log10(maxMemberNumber)+1);
        assertEquals(memberNumberLength, memLength);
    }

    //tests that boolean is returned from call to server connection class. Currently only returns true
    @Test
    public void testValidateMember() throws Exception {
        //assertTrue(billingMenu.validateMember(testMemberNumber));
    }


    @Test
    public void testLookUpServiceCode() throws Exception {

    }

    @Test
    public void testValidateServiceCode() throws Exception {

    }

    @Test
    public void testAddComments() throws Exception {

    }

    @Test
    public void testCollectBillingData() throws Exception {

    }

}