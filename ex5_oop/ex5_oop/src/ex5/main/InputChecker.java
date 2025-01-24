/**
 * This class provides methods to validate input parameters for the program.
 * Specifically, it ensures that a single file name is passed as input
 * and that the file has the correct ".sjava" suffix.
 */
package ex5.main;
import java.io.IOException;
import static ex5.main.Sjavac.IO_ERROR_EXIT_CODE;

public class InputChecker {
    // Constants for error messages and file suffix
    private static final String PARAMETERS_NUMBER_ERROR = "Invalid number of parameters.";
    private static final String INVALID_SUFFIX_ERROR = "File type is not .sjava.";
    private static final String SJAVA_VALID_SUFFIX = ".sjava";

    /**
     * Validates the input parameters provided to the program.
     * @param args The array of input arguments provided to the program.
     *             It should contain exactly one element: the file name.
     * @throws IOException If the input validation fails due to an invalid number of parameters
     *                     or an invalid file suffix.
     */
    public static void checkInputParameter(String[] args) {
        try {
            if (args.length != 1) {
                throw new IOException(PARAMETERS_NUMBER_ERROR);
            }
            String fileName = args[0];
            if (!fileName.endsWith(SJAVA_VALID_SUFFIX)) {
                throw new IOException(INVALID_SUFFIX_ERROR);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(IO_ERROR_EXIT_CODE);
        }
    }
}
