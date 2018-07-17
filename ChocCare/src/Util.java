import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Utility class.
 * Contains static methods that are useful but do not simulate network operations (eg. static methods used by the Customer class).
 * Created by gstone on 11/15/2016.
 */
public class Util {

    public static final int maxRenameAttempts = 10000;
    private static final File serviceRecordDirectory = new File("serviceRecords");

    //Forbid construction
    private Util() {}

    /**
     * Capitalizes the first letter of the string
     * @param str The string to capitalize
     * @return The string with the first letter capitalized
     */
    public static String capitalizeFirst(String str) {
        if (str.isEmpty()) return str;
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Ensures that directory exists and is a directory.
     * If the directory does not exist, creates it.
     * If there is another non-directory file with the name of the directory, prints a warning message to System.out, and attempts to rename it.
     * If either process fails, throws a runtime exception.
     * @param directory The directory to ensure the existence of.
     * @param description Description of the directory (eg. "service record folder"), for error messages (lowercase)
     */
    public static void ensureDirectoryExists(File directory, String description) {
        boolean directoryExists = directory.exists();
        if (!directoryExists) directoryExists = directory.mkdir();
        if (directoryExists && !directory.isDirectory()) {
            //There's an existent file blocking the directory from being created. Attempt to rename it.
            File moveCandidate = null;
            boolean foundTarget = false;
            for (int i = 0; i < maxRenameAttempts && !foundTarget; i++) {
                moveCandidate = new File(directory.getName() + i);
                if (!moveCandidate.exists()) foundTarget = true;
            }
            if (!foundTarget) {
                throw new RuntimeException(capitalizeFirst(description) + " " + directory.getPath() + " is a file, and could not find an unused filename to rename it to!");
            }
            System.out.println("WARNING: " + capitalizeFirst(description) + " " + directory.getPath() + " is a file! Renaming the file to " + moveCandidate.getPath());
            try {
                Files.move(directory.toPath(), moveCandidate.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException ex) {
                throw new RuntimeException("Could not rename file with same name as " + description + " " + directory.getPath() + " to " + moveCandidate.getPath(), ex);
            }
            directoryExists = directory.mkdir();
        }
        if (!directoryExists) {
            throw new RuntimeException("Could not create " + description + " " + directory.getPath());
        }
    }

    //Open a FileInputStream, but wrap IOException in RuntimeException
    public static FileInputStream openStreamNoThrow(File source) {
        try {
            return new FileInputStream(source);
        } catch (IOException ex) {
            throw new RuntimeException("Could not read XML file " + source.getPath(), ex);
        }
    }

    public static File getServiceRecordFile(BigInteger recordNum) {
        ensureDirectoryExists(serviceRecordDirectory, "service records folder");
        return new File(serviceRecordDirectory, recordNum + ".txt");
    }
}
