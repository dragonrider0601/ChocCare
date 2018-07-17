//Fadi Labib
//CS300 - Fall 2016
//ChocAn Software
//Started: 11/25/2016 7:00 PM
//Ended: In Progress

import java.io.IOException;

public class OperatorMenuManualTest {

    public static void main(String[] args) throws IOException {
        Menu menu = new OperatorMenu();
        menu.mainMenu();
        /*CURRENT ERROR: We are prompted to enter and ID Number forever */
        /* Issue has to do with the optional value being returned as no customers are actually being created yet */
        /*More details on this issue, check the to-do section in the OperatorMenu class */
    }
}
