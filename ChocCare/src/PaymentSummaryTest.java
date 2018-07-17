import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by Brandon Le on 11/11/16.
 * The purpose of this class is to conduct unit testing on for the PaymentSummary class.
 */
public class PaymentSummaryTest {
    private int MAX = 999999999;
    private int MAXINT = 2147483647;
    private ProviderPayment[] provpay = new ProviderPayment[]{
            new ProviderPayment("Bill", MAX, MAX, MAX),
            new ProviderPayment("Blake", MAX + 1, MAX * 2, MAX * 2),
            new ProviderPayment("abcdefghijklmnopqrstuvwxyz", MAXINT, MAXINT, MAXINT)
    };

    private PaymentSummary[] testPaymentSummary = new PaymentSummary[]{
            new PaymentSummary(),
    };

    /*Method only for testing if the contents of the list were added correctly and
    in the right order*/
    static public boolean isListCorrect(PaymentSummary paysum, ProviderPayment[] provpay){
        int numProviders = 3; //Number of providers to add
        int numCorrect = 0; //Number of adds that were correct

        for(int i = 0; i < numProviders; ++i){
            paysum.addPayment(provpay[i]);
        }

        for(int j = 0; j < numProviders; ++j){
            if(paysum.getPayments().get(j).getProviderName().equals(provpay[j].getProviderName()))
                ++numCorrect;
        }

        return numCorrect == numProviders; //Simplification of if(numCorrect == numProviders) return true; else return false;
    }

    /*Method for testing if the list was written out and displaying correctly.*/
    static public String isWriteCorrect(PaymentSummary paysum, ProviderPayment[] provpay){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream target = new PrintStream(output);
        int numProviders = 3;
        for(int i = 0; i < numProviders; ++i){
            paysum.addPayment(provpay[i]);
        }

        paysum.writeReport(target);
        target.close();
        return output.toString(); //Return the output as a string
    }


    private <T> void testFunction(T[] expected, Function<PaymentSummary, T> function, boolean ignoreNull) {
        assertEquals(expected.length, testPaymentSummary.length);
        for (int i = 0; i < testPaymentSummary.length; i++) {
            T result = function.apply(testPaymentSummary[i]);
            if (!(ignoreNull && expected[i] == null)) assertEquals(i + " " + testPaymentSummary[i].getPayments().get(0).getProviderName(), expected[i], result);
        }
    }

    @Test
    public void testAddPayment() throws Exception {
        Boolean[] expectedResults = { true
        };
        testFunction(expectedResults, (PaymentSummary p) -> isListCorrect(p, provpay), false);
    }

    @Test
    public void testWriteReport() throws Exception {
        String[] expectedResults = {"Provider name: " + "Bill" + "\n\t" +
                "Provider number: " + MAX + "\n\t" +
                "Number of consultations: " + MAX + "\n\t" +
                "Total service fee: " + MAX + "\n" +
            "Provider name: " + "Blake" + "\n\t" +
                "Provider number: " + (MAX + 1) + "\n\t" +
                "Number of consultations: " + (MAX * 2) + "\n\t" +
                "Total service fee: " + (MAX * 2) + "\n" +
            "Provider name: " + "abcdefghijklmnopqrstuvwxyz" + "\n\t" +
                "Provider number: " + MAXINT + "\n\t" +
                "Number of consultations: " + MAXINT + "\n\t" +
                "Total service fee: " + MAXINT + "\n"
        };
        testFunction(expectedResults, (PaymentSummary p) -> isWriteCorrect(p, provpay), false);
    }
}