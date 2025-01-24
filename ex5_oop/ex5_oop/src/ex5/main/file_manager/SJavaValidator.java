package ex5.main.file_manager;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * SJavaValidator class provides methods to validate different constructs in the simplified Java language (s-Java).
 * It uses regular expressions to match various patterns, including variable declarations, assignments, method declarations,
 * and control structures like if and while.
 */
public class SJavaValidator {
    // Method to check if a line matches the variable declaration format
    // Regular expression for valid s-Java variable declaration
    private static final String VARIABLE_FORMAT = "[a-zA-Z]\\w*";
    public static final String OPEN_BRACKET = "(";
    private static final String DECLARATION_REGEX =
            "^\\s*(final\\s+)?(int|double|String|boolean|char)\\s+" +  // Type with optional 'final'
                    OPEN_BRACKET + VARIABLE_FORMAT + "\\s*(=\\s*[^,;]+)?\\s*,\\s*)*" +  // Multiple variables
                    "[a-zA-Z_][a-zA-Z_\\d]*\\s*(=\\s*[^,;]+)?\\s*;\\s*$";     // Last variable and semicolon
    private static final Pattern DECLARATION_PATTERN = Pattern.compile(DECLARATION_REGEX);

    private static final String ASSIGNMENT_REGEX =
            "^\\s*(" + VARIABLE_FORMAT + ")\\s*=\\s*" +  // Variable name
                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+|[a-zA-Z_][a-zA-Z_\\d]*)\\s*" +  // Values or variables
                    "(?:,\\s*[a-zA-Z_][a-zA-Z_\\d]*\\s*=\\s*" +  // Multiple assignments
                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+|[a-zA-Z_][a-zA-Z_\\d]*)\\s*)*" +
                    ";\\s*$";
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(ASSIGNMENT_REGEX);
    private static final String IF = "if";
    private static final String WHILE = "while";
    private static final String CONDITIONAL_PATTERN = "^(if|while)\\s*\\(([^\\)]+)\\)\\s*\\{$";

    // Regex for a valid return statement:
    private static final String RETURN_REGEX = "^\\s*return\\s*;\\s*$";
    // Regex for a valid end-of-scope line:
    private static final String END_OF_SCOPE_REGEX = "^\\s*\\}\\s*$";
    private static final String METHOD_IDENTIFIER = "[a-zA-Z][a-zA-Z0-9_]*"; // Valid identifier (method name or variable)
    private static final String NUMERIC_CONST = "\\d+";
    private static final String STRING_LITERAL = "\\\".*?\\\"";
    private static final String CHAR_LITERAL = "\\'.*?\\'";
    private static final String OR = "|";
    private static final String CLOSE_REG_BRACE = ")*";
    private static final String ZERO_OR_MORE_COMMA_SEPARATED = "(\\s*,\\s*";
    private static final String ZERO_OR_MORE_WHITESPACES = "^\\s*";
    private static final String OPEN_PARENTHESIS_FORMAT_TRIMED = "\\s*\\(\\s*(";
    private static final String END_OF_METHOD = ")?\\s*\\)\\s*;\\s*$";
    private static final String INT_DOUBLE_BOOLEAN_CHAR_STRING = "int|double|boolean|char|String"; // Valid parameter types
    private static final String OPTIONAL_FINAL = "(?:final\\s+)?(";
    private static final String CLOSING_PARENTHESIS = ")\\s+";
    private static final String VOID = "^\\s*void\\s+";
    private static final String END_OF_METHOD_DECLARATION = ")?\\s*\\)\\s*\\{\\s*$";

    /**
     * Matches a given line against the variable declaration format (including optional 'final' and multiple variables).
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
                OPEN_BRACKET + METHOD_IDENTIFIER + OR + NUMERIC_CONST + OR + STRING_LITERAL + OR + CHAR_LITERAL + ")";
        // Parameters list: optional first parameter, followed by zero or more comma-separated parameters
        String parameters = parameter + ZERO_OR_MORE_COMMA_SEPARATED + parameter + CLOSE_REG_BRACE;
        // Full method call regex
        String methodCallRegex = ZERO_OR_MORE_WHITESPACES + METHOD_IDENTIFIER + OPEN_PARENTHESIS_FORMAT_TRIMED + parameters + END_OF_METHOD;

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(methodCallRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    /**
     * Matches a given line against the method declaration format, including the method name, return type, and parameters.
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
        String methodDeclarationRegex = VOID + identifier + OPEN_PARENTHESIS_FORMAT_TRIMED + parameters + END_OF_METHOD_DECLARATION;

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(methodDeclarationRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }


//    // Test the methods
//    public static void main(String[] args) {
//        // Test cases for method call format
//        String[] methodCallTestCases = {
//                "foo();",                       // Valid
//                "bar(5, \"hello\", varName);", // Valid
//                "doSomething(  );",             // Valid
//                "123foo();",                    // Invalid: method name
//                "foo(,);",                      // Invalid: empty parameter
//                "method(5,,6);"                 // Invalid: extra comma
//        };
//
//        System.out.println("Testing Method Call Format:");
//        for (String testCase : methodCallTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchMethodCallFormat(testCase));
//        }
//
//        // Test cases for method declaration format
//        String[] methodDeclarationTestCases = {
//                "void myMethod() {",                     // Valid
//                "void compute(int x, double y) {",       // Valid
//                "void doSomething(final boolean flag) {", // Valid
//                "int myMethod() {",                      // Invalid: not void
//                "void method(abc) {",                 // Invalid: invalid parameter name
//                "void method {"                          // Invalid: missing parentheses
//        };
//
//        System.out.println("\nTesting Method Declaration Format:");
//        for (String testCase : methodDeclarationTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchMethodDeclarationFormat(testCase));
//        }
//
//        // Test cases for return statement format
//        String[] returnTestCases = {
//                "return;",
//                " return ; ",
//                "return  ;",
//                "return",
//                "return something;",
//                "return ; extra"
//        };
//
//        System.out.println("\nTesting Return Statement Format:");
//        for (String testCase : returnTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchReturnFormat(testCase));
//        }
//
//        // Test cases for end-of-scope format
//        String[] endOfScopeTestCases = {
//                "}",          // Valid
//                " } ",        // Valid: extra spaces
//                "   }",       // Valid: spaces before
//                "}",          // Valid
//                "} extra",    // Invalid: additional content
//                "{",          // Invalid: opening brace
//                "",           // Invalid: empty line
//                "// }",       // Invalid: comment line
//        };
//
//        System.out.println("\nTesting End-of-Scope Format:");
//        for (String testCase : endOfScopeTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchEndOfScopeFormat(testCase));
//        }
//
//        // Test cases for if/while format
//        String[] ifWhileTestCases = {
//                "if (true) {",
//                "while (false) {",
//                "if (x) {",
//                "while (5.2) {",
//                "if (true && x || 5) {",
//                "if (true &&) {",
//                "if () {",
//                "if (a && b || ) {",
//                "if ((x) && y) {",
//                "while true) {",
//                "if (true) {}",
//        };
//
//        System.out.println("\nTesting If/While Format:");
//        for (String testCase : ifWhileTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchIfWhileFormat(testCase));
//        }
//
//        // Test cases for assignment format
//        String[] assignmentTestCases = {
//                "x = 5;",                  // Valid
//                "x = y;",                  // Valid
//                "x = true;",               // Valid
//                "x = \"hello\";",          // Valid
//                "x = 'c';",                // Valid
//                "x = 5, y = 10;",          // Valid: multiple assignments
//                "  x  =  5 ,  y  = 10  ;", // Valid: extra spaces
//                "x y = 5;",                // Invalid: missing comma
//                "x = 5, = 10;",            // Invalid: missing identifier
//                "x =;",                    // Invalid: missing value
//                "= 5;",                    // Invalid: missing identifier
//                "x = 5"                    // Invalid: missing semicolon
//        };
//
//        System.out.println("\nTesting Assignment Format:");
//        for (String testCase : assignmentTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchAssignmentFormat(testCase));
//        }
//
//        // Test cases for variable declaration format
//        String[] declarationTestCases = {
//                "int x;",                      // Valid: simple declaration
//                "double x = 5.5;",             // Valid: initialized declaration
//                "final boolean flag = true;",  // Valid: final declaration
//                "char a, b = 'c';",            // Valid: multiple declarations
//                "String s = \"hello\", t;",    // Valid: mixed initialization
//                "int 123x;",                   // Invalid: variable starts with digit
//                "float x;",                    // Invalid: unsupported type
//                "boolean a = false, = true;",  // Invalid: missing variable name
//                "int a, b,;",                  // Invalid: trailing comma
//                "final x;",                    // Invalid: missing type
//        };
//
//        System.out.println("\nTesting Declaration Format:");
//        for (String testCase : declarationTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchDeclarationFormat(testCase));
//        }
//    }
}
