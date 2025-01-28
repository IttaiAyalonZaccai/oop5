package ex5.main.file_manager;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * SJavaValidator class provides methods to validate different constructs in the
 * simplified Java language (s-Java).
 * It uses regular expressions to match various patterns, including variable declarations,
 * assignments, method declarations,
 * and control structures like if and while.
 */
public class SJavaValidator {
    // Method to check if a line matches the variable declaration format
    // Regular expression for valid s-Java variable declaration
    private static final String VARIABLE_FORMAT = "[a-zA-Z]\\w*";
    private static final String OPEN_BRACKET = "(";
    private static final String DECLARATION_REGEX =
            "^\\s*(final\\s+)?(int|double|String|boolean|char)\\s+" +  // Type with optional 'final'
                    OPEN_BRACKET + VARIABLE_FORMAT + "\\s*(=\\s*[^,;]+)?\\s*,\\s*)*" +  // Multiple variables
                    "[a-zA-Z_][a-zA-Z_\\d]*\\s*(=\\s*[^,;]+)?\\s*;\\s*$";     // Last variable and semicolon
    private static final Pattern DECLARATION_PATTERN = Pattern.compile(DECLARATION_REGEX);

    private static final String ASSIGNMENT_REGEX =
            "^\\s*(" + VARIABLE_FORMAT + ")\\s*=\\s*" +  // Variable name
                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?" +
                    "|[-+]?\\.\\d+|[a-zA-Z_][a-zA-Z_\\d]*)\\s*" +  // Values or variables
                    "(?:,\\s*[a-zA-Z_][a-zA-Z_\\d]*\\s*=\\s*" +  // Multiple assignments
                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+|" +
                    "[a-zA-Z_][a-zA-Z_\\d]*)\\s*)*" +
                    ";\\s*$";
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(ASSIGNMENT_REGEX);
    private static final String IF = "if";
    private static final String WHILE = "while";
    private static final String CONDITIONAL_PATTERN = "^(if|while)\\s*\\(([^\\)]+)\\)\\s*\\{$";

    // Regex for a valid return statement:
    private static final String RETURN_REGEX = "^\\s*return\\s*;\\s*$";
    // Regex for a valid end-of-scope line:
    private static final String END_OF_SCOPE_REGEX = "^\\s*\\}\\s*$";
    private static final String METHOD_IDENTIFIER = "[a-zA-Z][a-zA-Z0-9_]*"; // Valid identifier
    // (method name or variable)
    private static final String NUMERIC_CONST = "\\d+";
    private static final String STRING_LITERAL = "\\\".*?\\\"";
    private static final String CHAR_LITERAL = "\\'.*?\\'";
    private static final String OR = "|";
    private static final String CLOSE_REG_BRACE = ")*";
    private static final String ZERO_OR_MORE_COMMA_SEPARATED = "(\\s*,\\s*";
    private static final String ZERO_OR_MORE_WHITESPACES = "^\\s*";
    private static final String OPEN_PARENTHESIS_FORMAT_TRIMED = "\\s*\\(\\s*(";
    private static final String END_OF_METHOD = ")?\\s*\\)\\s*;\\s*$";
    private static final String INT_DOUBLE_BOOLEAN_CHAR_STRING = "int|double|boolean|char|String";
    // Valid parameter types
    private static final String OPTIONAL_FINAL = "(?:final\\s+)?(";
    private static final String CLOSING_PARENTHESIS = ")\\s+";
    private static final String VOID = "^\\s*void\\s+";
    private static final String END_OF_METHOD_DECLARATION = ")?\\s*\\)\\s*\\{\\s*$";
    private static final String CLOSE_PARENTHESISE = ")";

    /**
     * Matches a given line against the variable declaration format (including optional
     * 'final' and multiple variables).
     *
     * @param line The line of code to check.
     * @return true if the line matches the variable declaration format, otherwise false.
     */
    public static boolean matchDeclarationFormat(String line) {
        Matcher matcher = DECLARATION_PATTERN.matcher(line);
        return matcher.matches();
    }

    /**
     * Matches a given line against the variable assignment format (including multiple assignments).
     *
     * @param line The line of code to check.
     * @return true if the line matches the variable assignment format, otherwise false.
     */
    public static boolean matchAssignmentFormat(String line) {
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(line);
        return matcher.matches();
    }

    /**
     * Matches a given line against the 'if' or 'while' statement format.
     *
     * @param line The line of code to check.
     * @return true if the line matches the 'if' or 'while' format, otherwise false.
     */
    // Method to check if a line matches the if/while block format
    public static boolean matchIfWhileFormat(String line) {
        line = line.trim();
        if (line.startsWith(IF) || line.startsWith(WHILE)) {
            return line.matches(CONDITIONAL_PATTERN);
        }
        return false;
    }

    /**
     * Matches a given line against the valid 'return' statement format.
     *
     * @param line The line of code to check.
     * @return true if the line matches the 'return' format, otherwise false.
     */
    // Method to check if a line matches the return statement format
    public static boolean matchReturnFormat(String line) {
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(RETURN_REGEX);
        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Matches a given line against the valid end-of-scope format (closing brace).
     *
     * @param line The line of code to check.
     * @return true if the line matches the end-of-scope format, otherwise false.
     */
    // Method to check if a line matches the end-of-scope format
    public static boolean matchEndOfScopeFormat(String line) {
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(END_OF_SCOPE_REGEX);
        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Matches a given line against the method call format, including parameters and argument types.
     *
     * @param line The line of code to check.
     * @return true if the line matches the method call format, otherwise false.
     */
    // Method to check if a line matches the method call format
    public static boolean matchMethodCallFormat(String line) {
        // Constructing the regex for a method call format
        // Parameter regex
        String parameter =
                OPEN_BRACKET + METHOD_IDENTIFIER + OR + NUMERIC_CONST + OR + STRING_LITERAL
                        + OR + CHAR_LITERAL + CLOSE_PARENTHESISE;
        // Parameters list: optional first parameter, followed by zero or more comma-separated parameters
        String parameters = parameter + ZERO_OR_MORE_COMMA_SEPARATED + parameter + CLOSE_REG_BRACE;
        // Full method call regex
        String methodCallRegex = ZERO_OR_MORE_WHITESPACES + METHOD_IDENTIFIER +
                OPEN_PARENTHESIS_FORMAT_TRIMED + parameters + END_OF_METHOD;

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(methodCallRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Matches a given line against the method declaration format, including the method
     * name, return type, and parameters.
     *
     * @param line The line of code to check.
     * @return true if the line matches the method declaration format, otherwise false.
     */
    // Method to check if a line matches the method declaration format
    public static boolean matchMethodDeclarationFormat(String line) {
        // Constructing the regex for a method declaration format
        String identifier = METHOD_IDENTIFIER; // Valid method name
        // Parameter regex: optional 'final', type, and valid identifier
        String parameter = OPTIONAL_FINAL + INT_DOUBLE_BOOLEAN_CHAR_STRING + CLOSING_PARENTHESIS + identifier;
        // Parameters list: optional first parameter, followed by zero or more comma-separated parameters
        String parameters = parameter + ZERO_OR_MORE_COMMA_SEPARATED + parameter + CLOSE_REG_BRACE;
        // Full method declaration regex
        String methodDeclarationRegex = VOID + identifier + OPEN_PARENTHESIS_FORMAT_TRIMED +
                parameters + END_OF_METHOD_DECLARATION;

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(methodDeclarationRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }
}
