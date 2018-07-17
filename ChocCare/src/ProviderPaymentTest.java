import org.junit.Test;

import java.io.*;
import java.util.function.Function;
import static org.junit.Assert.*;

/**
 * Created by Brandon Le
 * Unit tests for ProviderPayment class. Tests must succeed when code is correct AND
 * fail when code is incorrect!
 */
public class ProviderPaymentTest {
    private int MAX = 999999999;
    //private int MAXINT = 2147483647;

    private ProviderPayment[] testProviderPayment = new ProviderPayment[]{
            new ProviderPayment("", 0, 0, 0),
            new ProviderPayment("abcdefghijklmnopqrstuvwxy", MAX + 1, MAX * 2, MAX * 2),
            new ProviderPayment("Normal", MAX, MAX, MAX)
    };

    //Method used to test if content from writeProviderNameNum was correct.
    private String isCorrectContent(ProviderPayment propay) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream target = new PrintStream(output);
        propay.writeProviderNameNum(target);
        target.close();
        return output.toString();
    }

    private <T> void testFunction(T[] expected, Function<ProviderPayment, T> function, boolean ignoreNull) throws FileNotFoundException {
        assertEquals(expected.length, testProviderPayment.length);
        for (int i = 0; i < testProviderPayment.length; i++) {
            T result = function.apply(testProviderPayment[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testProviderPayment[i].getProviderName(), expected[i], result);
        }
    }

    @Test
    /*Fails when overflow occurs for integers, but it should not happen because
    * max number for members and providers is 999999999 which is less than the
    * max value for an integer.
    */
    public void testWriteProviderNameNum() throws Exception {
        String[] expectedResults = {"Provider name: " + "" + "\n\t" +
                "Provider number: " + 0 + "\n\t" +
                "Number of consultations: " + 0 + "\n\t" +
                "Total service fee: " + 0 + "\n",

                "Provider name: " + "abcdefghijklmnopqrstuvwxy" + "\n\t" +
                        "Provider number: " + (MAX + 1) + "\n\t" +
                        "Number of consultations: " + (MAX * 2) + "\n\t" +
                        "Total service fee: " + (MAX * 2) + "\n",

                "Provider name: " + "Normal" + "\n\t" +
                        "Provider number: " + MAX + "\n\t" +
                        "Number of consultations: " + MAX + "\n\t" +
                        "Total service fee: " + MAX + "\n"
        };
        testFunction(expectedResults, (ProviderPayment p) -> isCorrectContent(p), false);
    }

    @Test
    public void testGetNumConsultations() throws Exception {
        Integer[] expectedResults = {
            0,
            MAX * 2,
            MAX
        };
        testFunction(expectedResults, ProviderPayment::getNumConsultations, false);
    }

    @Test
    public void testGetTotalServiceFee() throws Exception {
        //Generics don't identify data primitives like int and bool, so must spell out for example "Integer"
        //instead of "int"
        Integer[] expectedResults = {
                0,
                MAX * 2,
                MAX
        };
        testFunction(expectedResults, ProviderPayment::getTotalServiceFee, false);
    }
}