package ex5.main.file_manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ex5.main.Sjavac.SYNTAX_ERROR_EXIT_CODE;

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

    public static boolean isInMethodAssignment(String string) {
        // Regex pattern to match variable assignments inside a method
        String regex = "^\\s*[a-zA-Z_]\\w*\\s*=\\s*.*;$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the input string against the pattern
        Matcher matcher = pattern.matcher(string);

        // Return true if the input matches the assignment pattern
        return matcher.matches();
    }

//    public static void checkLineFormat(String line, int lineIndex) throws RuntimeException {
//        // is Declaration fomrat
//        if (line.matches("^\\s*final\\s+[a-zA-Z_]\\w*\\s+[a-zA-Z_]\\w*\\s*;$")) {}
//
//        // is Assignment format
//
//        // is If-While format
//        if (line.matches("^\\s*[a-zA-Z_]\\w*\\s*=\\s*[a-zA-Z_]\\w*\\s*;$")) {}
//
//        // is
//    }

    public static void checkLineFormat(String line, int lineIndex) {
        try {
//            // is Declaration format:
            if (SJavaValidator.matchDeclarationFormat(line)) {
                return;
            }
            // is Assignment format:
            if (SJavaValidator.matchAssignmentFormat(line)) {
                return;
            }
//            // is If-While format:
            if (SJavaValidator.matchIfWhileFormat(line)) {
                return;
            }
//            // is Method declaration format:
            if (SJavaValidator.matchMethodDeclarationFormat(line)) {
                return;
            }
//            // is Method call format:
            if (SJavaValidator.matchMethodCallFormat(line)) {
                return;
            }
            // is End-of-Scope format:
            if (SJavaValidator.matchEndOfScopeFormat(line)) {
                return;
            }
            // is Return format:
            if (SJavaValidator.matchReturnFormat(line)) {
                return;
            }
            throw new RuntimeException("ERROR in line " + lineIndex + " invalid line Format");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(SYNTAX_ERROR_EXIT_CODE);
        }
    }
}
