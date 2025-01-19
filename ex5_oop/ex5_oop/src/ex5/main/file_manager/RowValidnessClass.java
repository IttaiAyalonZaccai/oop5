package ex5.main.file_manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for validating lines based on specific patterns.
 */
public class RowValidnessClass {

    /**
     * Checks if a line ends with a valid suffix (e.g., ;, {}, or a comment).
     *
     * @param line        The line to check.
     * @param lineCounter The line number being validated.
     * @throws RuntimeException If the line does not end with a valid suffix.
     */
    public static void check_suffixes(String line, int lineCounter) throws RuntimeException {
        Pattern pattern = Pattern.compile(".*[;{}]\\s*$");
        Matcher mac = pattern.matcher(line);
        if (!mac.matches()) {
            throw new RuntimeException("ERROR in line " + lineCounter + " not supported comment value!");
        }
    }
    /**
     * Checks if there are unsupported comments (e.g., / *, * /, or //) in the middle of the line.
     *
     * @param line        The line to check.
     * @param lineCounter The line number being validated.
     * @throws RuntimeException If comments are found in the middle of the line.
     */
    public static void checkMiddleComments(String line, int lineCounter) throws RuntimeException {
        Pattern pattern = Pattern.compile("/\\*|\\*/|.//");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            throw new RuntimeException("ERROR in line " + lineCounter + " not supported comment value " +
                    "in the middle of the line!");
        }
    }

//    functions for function processing

    public static boolean isStartFunction(String line) throws RuntimeException {
        Pattern pattern = Pattern.compile("\\s*void.*");
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }
}
