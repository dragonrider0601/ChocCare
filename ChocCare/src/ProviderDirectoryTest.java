import java.io.*;
import java.util.Scanner;

import org.junit.Test;

/**
 * Provider directory testing
 * Created by Sam on 11/13/2016.
 */
public class ProviderDirectoryTest{

    private final int LOWEST_CODE=100000;
    private Service[] testList=new Service[]{
        new Service("Massage", LOWEST_CODE, 9999, true),
        new Service("Counseling", Integer.MAX_VALUE, Integer.MAX_VALUE, true),
        new Service("Hypnosis", 423192, Integer.MAX_VALUE, true),
        new Service("", LOWEST_CODE, 99, true),
        new Service("", Integer.MAX_VALUE, Integer.MAX_VALUE, false)
    };

    @Test
    public void testClass() throws IOException {
        //public ProviderDirectoryTest(){}
        Scanner reader = new Scanner(System.in);
        Scanner reader2 = new Scanner(System.in);

        File f = new File("services.xml");

        ProviderDirectory pr = new ProviderDirectory(f);

        for (int i = 0; i < testList.length; i++) {
            pr.addService(testList[i]);
        }
        pr.displayAll(System.out);
        pr.displayAll(new PrintStream(new File("output.txt")));
        pr.save(f);
    }

}
