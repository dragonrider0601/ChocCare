import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by Brandon Le on 11/17/16.
 * This class is used to test ServiceRecord class methods.
 */
public class ServiceRecordTest {
    /*An array of serviceDates to test*/
    private Date[] testDate = {
            new Date(0),
            new Date(10000000),
            new Date(1000000000),
            new Date(2000000000)
    };

    /*An array of 9-digit providerNumbers to test*/
    private int[] testProviderNumber = {
            1000,
            10000,
            100000,
            9999999
    };

    /*An array of 9-digit memberNumbers to test*/
    private int[] testMemberNumber = {
            100000000,
            200000000,
            300000000,
            999999999
    };

    /*An array of 6-digit serviceCodes to test*/
    private int[] testServiceCode = {
            0,
            100000,
            123456,
            999999
    };

    /*An array of fees to test*/
    private int[] testFee = {
            0,
            10000,
            100000000,
            999999999
    };

    /*An array of comments to test*/
    private String[] testComment = {
            "",
            "How do you add this?",
            "THE ANSWER WORKS IN EVERY CASE I THINK!",
            "Well done, good job."
    };

    /*An array of ServiceRecords to test*/
    private ServiceRecord[] testServiceRecord = {
            new ServiceRecord(testDate[0], testProviderNumber[0],
                    testMemberNumber[0], testServiceCode[0], testFee[0], testComment[0]),
                new ServiceRecord(testDate[1], testProviderNumber[1],
                    testMemberNumber[1], testServiceCode[1], testFee[1], testComment[1]),
                new ServiceRecord(testDate[2], testProviderNumber[2],
                    testMemberNumber[2], testServiceCode[2], testFee[2], testComment[2]),
                new ServiceRecord(testDate[3], testProviderNumber[3],
                    testMemberNumber[3], testServiceCode[3], testFee[3], testComment[3])
    };

    /**
     * Tests that the results of function are equal to the expected values.
     * @param expected The array of expected values
     * @param function The function to test
     * @param ignoreNull If true, accept any value from function (other than throwing an exception) when the value in expected is null
     * @param <T> The return type of the function
     */
    private <T> void testFunction(T[] expected, Function<ServiceRecord, T> function, boolean ignoreNull) {
        assertEquals("Expected array should have the same length as the length of tested objects", expected.length, testServiceRecord.length);
        for (int i = 0; i < testServiceRecord.length; i++) {
            T result = function.apply(testServiceRecord[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testServiceRecord[i].getRecordNumber(), expected[i], result);
        }
    }

    /*Method to test if ServiceRecord write is correct. This method grabs the file contents.*/
    static public String getFileContent(ServiceRecord fromFile){
        String out = "Service record number: " + fromFile.getRecordNumber().shiftRight(32).toString() + "\n\t" +
                "Service date and time: " + fromFile.getServiceDate().toString() + "\n\t" +
                "Provider number: " + fromFile.getProviderNumber() + "\n\t" +
                "Member number: " + fromFile.getMemberNumber() + "\n\t" +
                "Service code: " + fromFile.getServiceCode() + "\n\t" +
                "Service fee: " + fromFile.getFee() + "\n\t" +
                "Service comment: " + fromFile.getComment() + "\n";

        return out;
    }

    /*Method to test if ServiceRecord is being written out to file correctly by
    * grabbing the contents of the file after the write*/
    static public String isWriteCorrect(ServiceRecord servrec){
        File fileInput = new File("testServiceRecord.xml");
        servrec.saveServiceToFile(fileInput);
        ServiceRecord fromFile = new ServiceRecord(fileInput);
        return getFileContent(fromFile);
    }

    /*Test for the saveServiceToFile method as well as the writeService method
    * which is used by the saveServiceToFile method.*/
    @Test
    public void testSaveServiceToFile() throws Exception {
          String[] expectedResults = {
                "Service record number: 4294967296344\n\t" +
                "Service date and time: Wed Dec 31 16:00:00 PST 1969\n\t" +
                "Provider number: 1000\n\t" +
                "Member number: 100000000\n\t" +
                "Service code: 0\n\t" +
                "Service fee: 0\n\t" +
                "Service comment: \n",

                "Service record number: 42949672960344\n\t" +
                "Service date and time: Wed Dec 31 18:46:40 PST 1969\n\t"+
                "Provider number: 10000\n\t" +
                "Member number: 200000000\n\t" +
                "Service code: 100000\n\t" +
                "Service fee: 10000\n\t" +
                "Service comment: How do you add this?\n",

                "Service record number: 429496729600344\n\t" +
                "Service date and time: Mon Jan 12 05:46:40 PST 1970\n\t" +
                "Provider number: 100000\n\t" +
                "Member number: 300000000\n\t" +
                "Service code: 123456\n\t" +
                "Service fee: 100000000\n\t" +
                "Service comment: THE ANSWER WORKS IN EVERY CASE I THINK!\n",

                "Service record number: 42949668665033048\n\t" +
                "Service date and time: Fri Jan 23 19:33:20 PST 1970\n\t" +
                "Provider number: 9999999\n\t" +
                "Member number: 999999999\n\t" +
                "Service code: 999999\n\t" +
                "Service fee: 999999999\n\t" +
                "Service comment: Well done, good job.\n"
        };
        testFunction(expectedResults, (ServiceRecord s) -> isWriteCorrect(s), false);
    }

    /*Test for the getRecordNumber method. Test used a shift right to ignore
    * lower 32 bits which constantly change.*/
    @Test
    public void getRecordNumber() throws Exception {
        BigInteger[] expectedResults = {
                new BigInteger("4294967296344"),
                new BigInteger("42949672960344"),
                new BigInteger("429496729600344"),
                new BigInteger("42949668665033048")
        };
        testFunction(expectedResults, (ServiceRecord s) -> s.getRecordNumber().shiftRight(32) , false);
    }

    /*Test for the getProviderNumber method.*/
    @Test
    public void testGetProviderNumber() throws Exception {
        Integer[] expectedResults = {
                1000,
                10000,
                100000,
                9999999
        };
        testFunction(expectedResults, ServiceRecord::getProviderNumber, false);
    }

    /*Test for the getMemberNumber method.*/
    @Test
    public void testGetMemberNumber() throws Exception {
        Integer[] expectedResults = {
                100000000,
                200000000,
                300000000,
                999999999
        };
        testFunction(expectedResults, ServiceRecord::getMemberNumber, false);
    }

    /*Test for the getFee method.*/
    @Test
    public void testGetFee() throws Exception {
        Integer[] expectedResults = {
                0,
                10000,
                100000000,
                999999999
        };
        testFunction(expectedResults, ServiceRecord::getFee, false);
    }

    /*Test for the getServiceDate method.*/
    @Test
    public void testGetServiceDate() throws Exception {
        Date[] expectedResults = {
                new Date(0),
                new Date(10000000),
                new Date(1000000000),
                new Date(2000000000)
        };
        testFunction(expectedResults, ServiceRecord::getServiceDate, false);
    }

}