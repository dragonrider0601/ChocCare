import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

//Fadi Labib
//CS300 - Fall 2016
//ChocAn Software
//Started: 11/24/2016 8:00 AM
//Ended: 11/26/2016 9:30 PpM

public class MemberManualTest {

    static Member[] testMembers;

    public static void main(String[] args) throws IOException {
        deleteMemberNumberFile();
        System.out.println(new File("").getAbsolutePath());

        testMembers = new Member[]{
                new Member("TestProvider0", "street addr", "OR", "Portland", "97229", Customer.Status.EXPIRED),
                new Member("LargeProviderData","street address that has a total of 54 characters in it", "Washington D.C.", "Winchester-on-the-Severn", "97229-1234", Customer.Status.PAID),
                new Member("BlankAddress", "", "", "", "", Customer.Status.PAID),
                new Member("","Blank Name", "me", "Portland", "04103", Customer.Status.EXPIRED),
                new Member("MiddlingMember", "addr", "tX", "Houston", "12345", Customer.Status.PAID),
                new Member("ProviderInvalidStatus","Clinic", "OR", "Medford", "54321", Customer.Status.PAID),
        };

        Date start = new Date(Menu.promptLong("Enter the start time: "));
        Date end = new Date(Menu.promptLong("Enter the end time: "));
        File filepath = new File("../test members");
        writeAllReports(filepath, start, end);
        File [] fileList = filepath.listFiles();
        ProviderManualTest.readFiles(fileList);
        ProviderManualTest.deleteFiles(fileList);
    }

    public static void deleteMemberNumberFile() {
        File f = new File("maxMemberNumber.txt");
        f.delete();
    }

    private static void writeAllReports(File directory, Date startDate, Date endDate) throws IOException {
        Util.ensureDirectoryExists(directory, "member reports folder");
        for (Member m : testMembers) {
            File target = new File(directory, m.id + ".txt");
            if (!target.createNewFile()) {
                System.out.println("File " + m.id + ".txt couldn't be created");
            }
            PrintStream targetStream = new PrintStream(target);
            m.writeReport(targetStream, startDate, endDate);
        }
    }
}
