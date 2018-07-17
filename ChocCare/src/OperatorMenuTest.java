import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Flabib on 11/19/2016.
 */

//Values here are not real values Yet!
public class OperatorMenuTest {

    @Test
    public void testMainMenu() throws Exception {

    }

    @Test
    void addCustomer(boolean isMember) {

    }

    @Test
    boolean isAddMember(){
        return true;
    }

    @Test
    void addMember(){

    }

    @Test
    void addProvider(){

    }

    @Test
    void updateCustomerData(Customer customer) {

    }

    @Test
    Customer customerToUpdate() throws IOException {
        Member member = new Member("Person", "Street Address", "Portland", "Oregon", "97216", Member.Status.PAID);
        return member;
    }

    @Test
    Customer.Status getCustomerStatusInput(){
        return Customer.Status.PAID;
    }

    @Test
    String getUserInput(){
        return "c";
    }

    @Test
    int getCustomerIdInput(){
        return 10000002;
    }


}