package ex5.main.file_manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ex5.main.Sjavac.SYNTAX_ERROR_EXIT_CODE;

/**
 * Utility class for validating lines based on specific patterns.
 */
public class RowValidnessClass {

    private static final String ERROR_UNSUPPORTED_COMMENT = "ERROR in line %d not supported comment value!";
    private static final String ERROR_UNSUPPORTED_MIDDLE_COMMENT = "ERROR in line %d not supported " +
            "comment value in the middle of the line!";
    private static final String ERROR_INVALID_LINE_FORMAT = "ERROR in line %d invalid line Format";

    private static final String SUFFIX_PATTERN = ".*[;{}]\\s*$";
    private static final String MIDDLE_COMMENT_PATTERN = "/\\*|\\*/|.//";
    private static final String START_FUNCTION_PATTERN = "\\s*void.*";
    private static final String IN_METHOD_ASSIGNMENT_PATTERN = "^\\s*[a-zA-Z_]\\w*\\s*=\\s*.*;$";

    /**
     * Checks if a line ends with a valid suffix (e.g., ;, {}, or a comment).
     *
     * @param line        The line to check.
     * @param lineCounter The line number being validated.
     * @throws SyntaxException If the line does not end with a valid suffix.
     */
    public static void checkSuffixes(String line, int lineCounter) throws SyntaxException {
        Pattern pattern = Pattern.compile(SUFFIX_PATTERN);
        Matcher mac = pattern.matcher(line);
        if (!mac.matches()) {
            throw new SyntaxException(String.format(ERROR_UNSUPPORTED_COMMENT, lineCounter));
        }
    }

    /**
     * Checks if there are unsupported comments (e.g., / *, * /, or //) in the middle of the line.
     *
     * @param line        The line to check.
     * @param lineCounter The line number being validated.
     * @throws SyntaxException If comments are found in the middle of the line.
     */
    public static void checkMiddleComments(String line, int lineCounter) throws SyntaxException {
        Pattern pattern = Pattern.compile(MIDDLE_COMMENT_PATTERN);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            throw new SyntaxException(String.format(ERROR_UNSUPPORTED_MIDDLE_COMMENT, lineCounter));
        }
    }

    /**
     * Checks if the given line starts a function definition.
     *
     * @param line The line to check.
     * @return true if the line starts a function definition, false otherwise.
     */
    public static boolean isStartFunction(String line) {
        Pattern pattern = Pattern.compile(START_FUNCTION_PATTERN);
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Checks if the given string matches the format of an assignment inside a method.
     *
     * @param string The string to check.
     * @return true if the string matches an assignment format, false otherwise.
     */
    public static boolean isInMethodAssignment(String string) {
        Pattern pattern = Pattern.compile(IN_METHOD_ASSIGNMENT_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    /**
     * Checks the format of a given line using various s-Java validation rules.
     *
     * @param line      The line to check.
     * @param lineIndex The line number being validated.
     */
    public static void checkLineFormat(String line, int lineIndex) {
        try {
            if (SJavaValidator.matchDeclarationFormat(line)) {
                return;
            }
            if (SJavaValidator.matchAssignmentFormat(line)) {
                return;
            }
            if (SJavaValidator.matchIfWhileFormat(line)) {
                return;
            }
            if (SJavaValidator.matchMethodDeclarationFormat(line)) {
                return;
            }
            if (SJavaValidator.matchMethodCallFormat(line)) {
                return;
            }
            if (SJavaValidator.matchEndOfScopeFormat(line)) {
                return;
            }
            if (SJavaValidator.matchReturnFormat(line)) {
                return;
            }
            throw new SyntaxException(String.format(ERROR_INVALID_LINE_FORMAT, lineIndex));
        } catch (SyntaxException e) {
            System.out.println(SYNTAX_ERROR_EXIT_CODE);
            System.out.println(e.getMessage());
            System.exit(SYNTAX_ERROR_EXIT_CODE);
        }
    }
}
