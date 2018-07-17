import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Scanner;

//Fadi Labib
//CS300 - Fall 2016
//ChocAn Software
//Started: 11/24/2016 8:00 AM
//Ended: 11/26/2016 9:30 PpM

public class ProviderManualTest {

    static Provider[] testProviders;

    public static void main(String[] args) throws IOException {

        deleteProviderNumberFile(); //here is the deleting maxProviderNumber function
        System.out.println(new File("").getAbsolutePath());

        testProviders = new Provider[]{
                new Provider("TestProvider0", "street addr", "OR", "Portland", "97229", Customer.Status.OUT_OF_NETWORK),
                new Provider("LargeProviderData","street address that has a total of 54 characters in it", "Washington D.C.", "Winchester-on-the-Severn", "97229-1234", Customer.Status.IN_NETWORK),
                new Provider("BlankAddress", "", "", "", "", Customer.Status.IN_NETWORK),
                new Provider("","Blank Name", "me", "Portland", "04103", Customer.Status.OUT_OF_NETWORK),
                new Provider("MiddlingMember", "addr", "tX", "Houston", "12345", Customer.Status.IN_NETWORK),
                new Provider("ProviderInvalidStatus","Clinic", "OR", "Medford", "54321", Customer.Status.IN_NETWORK),
        };

        PaymentSummary paySummary;
        Date start = new Date(Menu.promptLong("Enter the start time: "));
        Date end = new Date(Menu.promptLong("Enter the end time: "));
        File filepath = new File("../test providers");
        paySummary = writeAllReports(filepath, start, end);
        File [] fileList = filepath.listFiles();
        readFiles(fileList);
        paySummary.writeReport(System.out);
        deleteFiles(fileList);
    }

    public static void readFiles(File [] fileList){
        if(fileList != null){
            for(File target : fileList){
                if(target.getName().matches("[0-9]+\\.txt")){
                    try{
                        Scanner sc = new Scanner(target);
                        while(sc.hasNextLine()){
                            String line = sc.nextLine();
                            System.out.println(line);
                        }
                        sc.close();
                    }catch(IOException ex){
                        throw new RuntimeException("Couldn't read file", ex);
                    }
                    System.out.println("===============================================================");
                }//end if
            }//end for

        }//end file reading
    }

    public static void deleteProviderNumberFile() {
        File f = new File("maxProviderNumber.txt");
            f.delete();
    }

    public static void deleteFiles(File [] fileList){
        if(fileList != null){
            boolean success = false;
            for(File target : fileList){
                if(target.getName().matches("[0-9]+\\.txt")){
                    if(target.delete()){
                        success = true;
                    }
                }//end extension check
            }//end for
            if(success){
                System.out.println("All files have been deleted successfully");
            }
        }//end delete
    }//end deleteFiles

    private static PaymentSummary writeAllReports(File directory, Date startDate, Date endDate) throws IOException {
         PaymentSummary summary = new PaymentSummary();
         //ensure that a directory exist before creating files
         Util.ensureDirectoryExists(directory, "provider reports folder");
         for (Provider p : testProviders) {
             File target = new File(directory, p.id + ".txt");
             if(!target.createNewFile()){
                 System.out.println("File " + p.id + ".txt couldn't be created");
             }
             PrintStream targetStream = new PrintStream(target);
             summary.addPayment(p.writeProviderReport(targetStream, startDate, endDate));
         }
         return summary;
    }//end writeAllReports
}
