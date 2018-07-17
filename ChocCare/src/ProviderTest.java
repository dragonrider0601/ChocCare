import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by Flabib on 11/11/2016.
 */
public class ProviderTest {

    @Before
    public void setUp() throws IOException {
        PrintWriter write;
        write = new PrintWriter("maxProviderNumber.txt");
        write.println(999);
        write.close();

        testProviders = new Provider[]{
                new Provider("TestCustomer0","street addr", "OR", "Portland", "97229", Provider.Status.IN_NETWORK) {},
                new Provider("LargeCustomerData", "street address that has a total of 54 characters in it", "Washington D.C.", "Winchester-on-the-Severn", "97229-1234", Provider.Status.OUT_OF_NETWORK) {},
                new Provider("BlankAddress", "", "", "", "", Provider.Status.IN_NETWORK) {},
        };
    }

    public static Provider[] testProviders;

    /**
     * Tests that the results of function are equal to the expected values.
     * @param expected The array of expected values
     * @param function The function to test
     * @param ignoreNull If true, accept any value from function (other than throwing an exception) when the value in expected is null
     * @param <T> The return type of the function
     */
    private <T> void testFunction(T[] expected, Function<Customer, T> function, boolean ignoreNull) {
        assertEquals(expected.length, testProviders.length);
        for (int i = 0; i < testProviders.length; i++) {
            T result = function.apply(testProviders[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testProviders[i].name, expected[i], result);
        }
    }

    @Test
    public void testProviderNumberPattern() throws Exception{
        int initialProviderNum = Provider.getMaxProviderNumber();
        assertEquals("InCorrect Initial Number!", 999 + testProviders.length, initialProviderNum);
        for(int i = 0; i < 10; i++){
            assertEquals("Numbers are not the same" ,Provider.reserveNextProviderNumber(),Provider.getMaxProviderNumber());
            assertEquals("initial number and max provider num are no longer the same!", initialProviderNum + i + 1, Provider.getMaxProviderNumber());
        }
    }

    @Test
    public void testWriteProviderReport() throws Exception {

    }

    @Test
    public void testWriteReport() throws Exception {

    }
}