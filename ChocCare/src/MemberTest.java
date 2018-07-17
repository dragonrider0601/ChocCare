import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by Flabib on 11/18/2016.
 */
public class MemberTest {

    @Before
    public void setUp() throws Exception {
        PrintWriter write;
        write = new PrintWriter("maxMemberNumber.txt");
        write.println(99999999);
        write.close();

        testMembers = new Member[]{
                new Member("TestCustomer0","street addr", "OR", "Portland", "97229", Member.Status.PAID) {},
                new Member("LargeCustomerData", "street address that has a total of 54 characters in it", "Washington D.C.", "Winchester-on-the-Severn", "97229-1234", Member.Status.EXPIRED) {},
                new Member("BlankAddress", "", "", "", "", Member.Status.PAID) {},
        };
    }

    private Member[] testMembers;

    /**
     * Tests that the results of function are equal to the expected values.
     * @param expected The array of expected values
     * @param function The function to test
     * @param ignoreNull If true, accept any value from function (other than throwing an exception) when the value in expected is null
     * @param <T> The return type of the function
     */
    private <T> void testFunction(T[] expected, Function<Customer, T> function, boolean ignoreNull) {
        assertEquals(expected.length, testMembers.length);
        for (int i = 0; i < testMembers.length; i++) {
            T result = function.apply(testMembers[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testMembers[i].name, expected[i], result);
        }
    }

    @Test
    public void testProviderNumberPattern() throws Exception{
        int initialMemberNum = Member.getMaxMemberNumber();
        assertEquals("InCorrect Initial Number!", 99999999 + testMembers.length, initialMemberNum);
        for(int i = 0; i < 10; i++){
            assertEquals("Numbers are not the same" ,Member.reserveNextMemberNumber(),Member.getMaxMemberNumber());
            assertEquals("initial number and max provider num are no longer the same!", initialMemberNum + i + 1, Member.getMaxMemberNumber());
        }
    }

    @Test
    public void testWriteReport() throws Exception {

    }

    @Test
    public void testWriteAllReports() throws Exception {

    }
}