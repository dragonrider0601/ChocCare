import java.io.*;

/**
 *Created by Brandon Le on 11/10/16.
 *The PaymentSummary is the class that generates the reports for the manangers.
 */
public class ProviderPayment {
    private String providerName; //Provider name (25 characters)
    private int providerNumber; //Provider (9 digits)
    private int numConsultations; //Total number of consultations for the provider
    private int totalServiceFee; //Total Service fee for each provider

    /*Constructor used to construct a ProviderPayment object from input data. */
    public ProviderPayment(String providerName, int providerNumber, int numConsultations, int  totalServiceFee) {
        this.providerName = providerName;
        this.providerNumber = providerNumber;
        this.numConsultations = numConsultations;
        this.totalServiceFee = totalServiceFee;
    }

    /*Method for writing all parts of a providers payment info for the report for a
    ProviderPayment to a plain text document. */
    public void writeProviderNameNum(PrintStream target){
        //TODO explain what data you're writing!! Like with service record
        String toAppend = "Provider name: " + providerName + "\n\t" +
                "Provider number: " + providerNumber + "\n\t" +
                "Number of consultations: " + numConsultations + "\n\t" +
                "Total service fee: " + totalServiceFee + "\n";
        target.print(toAppend);
        //target.close();
    }

    /*Method to get the numConsultations. */
    public int getNumConsultations(){
        return numConsultations;
    }

    /*Method to get the totalServiceFee. */
    public int getTotalServiceFee(){
        return totalServiceFee;
    }


    //Methods for testing purposes ONLY.
    public String getProviderName() {
        return providerName;
    }

    /*Method to get providerNumber. */
    public int getProviderNumber() {
        return providerNumber;
    }

    /*Method to display a ProviderPayment object's fields. */
    public void displayProviderPayment() {
        System.out.println(providerName + " " +
                providerNumber + " " +
                numConsultations + " " +
                totalServiceFee + "\n");
    }

    /*Methods solely for testing*/
    /*public static void main(String[] args) throws IOException {
        PrintStream fileout = new PrintStream(new FileOutputStream("testProviderPayment.txt", true));
        ProviderPayment billy = new ProviderPayment("Billy", 123456789, 5, 20000);
        String path = "testProviderPayment.txt";
        System.out.println("Provider Name: " + billy.getProviderName() + "\n" +
                "Provider Number: " + billy.getProviderNumber() + "\n" +
                "Number of consultations: " + billy.getNumConsultations() + "\n" +
                "Total Service Fee: " + billy.getTotalServiceFee() + "\n");

        billy.writeProviderNameNum(fileout); //Prints data to file
        billy.writeProviderNameNum(System.out); //Prints to screen

        if(isEqualFile("Billy" + "\n\t" +
                12222443 + "\n\t" +
                5 + "\n\t" +
                200000 + "\n"))
            System.out.println("Same!");
        fileout.close();
    }*/
}
