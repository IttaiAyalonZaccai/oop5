package ex5.main.file_manager;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

public class SJavaValidator {
    // Method to check if a line matches the variable declaration format
    // Regular expression for valid s-Java variable declaration
    private static final String VARIABLE_FORMAT = "[a-zA-Z]\\w*";
    private static final String DECLARATION_REGEX =
            "^\\s*(final\\s+)?(int|double|String|boolean|char)\\s+" +  // Type with optional 'final'
                    "(" + VARIABLE_FORMAT+ "\\s*(=\\s*[^,;]+)?\\s*,\\s*)*" +  // Multiple variables
                    "[a-zA-Z_][a-zA-Z_\\d]*\\s*(=\\s*[^,;]+)?\\s*;\\s*$";     // Last variable and semicolon
    private static final Pattern DECLARATION_PATTERN = Pattern.compile(DECLARATION_REGEX);
    // Regular expression for valid s-Java variable assignment
//    private static final String ASSIGNMENT_REGEX =
//            "^\\s*([a-zA-Z_][a-zA-Z_\\d]*)\\s*=\\s*" +   // Variable name
//                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+)\\s*" + // Assigned value (String, char, boolean, int, double)
//                    "(?:,\\s*[a-zA-Z_][a-zA-Z_\\d]*\\s*=\\s*" +   // Multiple assignments
//                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+)\\s*)*" +
//                    ";\\s*$";

    private static final String ASSIGNMENT_REGEX =
            "^\\s*(" + VARIABLE_FORMAT + ")\\s*=\\s*" +  // Variable name
                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+|[a-zA-Z_][a-zA-Z_\\d]*)\\s*" +  // Values or variables
                    "(?:,\\s*[a-zA-Z_][a-zA-Z_\\d]*\\s*=\\s*" +  // Multiple assignments
                    "(?:\"[^\"]*\"|'[^']'|true|false|[-+]?\\d+(\\.\\d+)?|[-+]?\\.\\d+|[a-zA-Z_][a-zA-Z_\\d]*)\\s*)*" +
                    ";\\s*$";
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(ASSIGNMENT_REGEX);


    public static boolean matchDeclarationFormat(String line) {
        Matcher matcher = DECLARATION_PATTERN.matcher(line);
        return matcher.matches();
    }
//    public static boolean matchDeclarationFormat(String line) { // todo remove
//        // Valid types in s-Java
//        String validTypes = "int|double|boolean|char|String";
//
//        // Matches variable names
//        String variableNamePattern = "[a-zA-Z_][a-zA-Z0-9_]*";
//        // Matches valid values (numbers, booleans, strings, characters, or variables)
//        String valuePattern = "\\d+(\\.\\d+)?|true|false|\"[^\"]*\"|'[^']'|" + variableNamePattern;
//        // Matches a single variable declaration (with optional initialization)
//        String singleVariablePattern = String.format("%s(\\s*=\\s*%s)?", variableNamePattern, valuePattern);
//        // Matches multiple variables in the same declaration (comma-separated)
//        String multipleVariablesPattern = String.format("%s(\\s*,\\s*%s)*", singleVariablePattern, singleVariablePattern);
//        // Matches a complete variable declaration line
//        String declarationPattern = String.format(
//                "^(final\\s+)?(%s)\\s+%s\\s*;$",
//                validTypes, multipleVariablesPattern
//        );
//
//        // Compile and match the regex
//        Pattern pattern = Pattern.compile(declarationPattern);
//        Matcher matcher = pattern.matcher(line);
//        return matcher.matches();
//    }



    public static boolean matchAssignmentFormat(String line) {
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(line);
        return matcher.matches();
    }

    // Method to check if a line matches the if/while block format
    public static boolean matchIfWhileFormat(String line) {
        line = line.trim();
        if(line.startsWith("if") || line.startsWith("while")) {
            String conditionalPattern = "^(if|while)\\s*\\(([^\\)]+)\\)\\s*\\{$";
            return line.matches(conditionalPattern);
        }
        return false;

//        // todo it is not working despite the regex looks good...
//        String identifier = "[a-zA-Z][a-zA-Z0-9_]*";
//        String conditionValue = "true|false|\\d+(\\.\\d+)?|" + identifier;
//        String condition = conditionValue + "(\\s*(\\|\\| | &&)\\s*" + conditionValue + ")*";
//        String ifWhileRegex = "^\\s*(if|while)\\s*\\(\\s*" + condition + "\\s*\\)\\s*\\{\\s*$";
//
//        Pattern pattern = Pattern.compile(ifWhileRegex);
//        Matcher matcher = pattern.matcher(line);
//        return matcher.matches();
    }

    // Method to check if a line matches the return statement format
    public static boolean matchReturnFormat(String line) {
        // Regex for a valid return statement
        String returnRegex = "^\\s*return\\s*;\\s*$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(returnRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    // Method to check if a line matches the end-of-scope format
    public static boolean matchEndOfScopeFormat(String line) {
        // Regex for a valid end-of-scope line
        String endOfScopeRegex = "^\\s*\\}\\s*$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(endOfScopeRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    // Method to check if a line matches the method call format
    public static boolean matchMethodCallFormat(String line) {
        // Constructing the regex for a method call format
        String identifier = "[a-zA-Z][a-zA-Z0-9_]*"; // Valid identifier (method name or variable)
        String constant = "\\d+";                    // Numeric constant
        String stringLiteral = "\\\".*?\\\"";       // String literal
        String charLiteral = "\\'.*?\\'";             // Character literal
        // Parameter regex
        String parameter = "(" + identifier + "|" + constant + "|" + stringLiteral + "|" + charLiteral + ")";
        // Parameters list: optional first parameter, followed by zero or more comma-separated parameters
        String parameters = parameter + "(\\s*,\\s*" + parameter + ")*";
        // Full method call regex
        String methodCallRegex = "^\\s*" + identifier + "\\s*\\(\\s*(" + parameters + ")?\\s*\\)\\s*;\\s*$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(methodCallRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    // Method to check if a line matches the method declaration format
    public static boolean matchMethodDeclarationFormat(String line) {
        // Constructing the regex for a method declaration format
        String identifier = "[a-zA-Z][a-zA-Z0-9_]*"; // Valid method name
        String type = "int|double|boolean|char|String"; // Valid parameter types
        // Parameter regex: optional 'final', type, and valid identifier
        String parameter = "(?:final\\s+)?(" + type + ")\\s+" + identifier;
        // Parameters list: optional first parameter, followed by zero or more comma-separated parameters
        String parameters = parameter + "(\\s*,\\s*" + parameter + ")*";
        // Full method declaration regex
        String methodDeclarationRegex = "^\\s*void\\s+" + identifier + "\\s*\\(\\s*(" + parameters + ")?\\s*\\)\\s*\\{\\s*$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(methodDeclarationRegex);

        // Match the line against the pattern
        Matcher matcher = pattern.matcher(line);
        return matcher.matches();
    }

    // Test the methods
    public static void main(String[] args) {
        // Test cases for method call format
        String[] methodCallTestCases = {
                "foo();",                       // Valid
                "bar(5, \"hello\", varName);", // Valid
                "doSomething(  );",             // Valid
                "123foo();",                    // Invalid: method name
                "foo(,);",                      // Invalid: empty parameter
                "method(5,,6);"                 // Invalid: extra comma
        };

//        System.out.println("Testing Method Call Format:");
//        for (String testCase : methodCallTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchMethodCallFormat(testCase));
//        }

        // Test cases for method declaration format
        String[] methodDeclarationTestCases = {
                "void myMethod() {",                     // Valid
                "void compute(int x, double y) {",       // Valid
                "void doSomething(final boolean flag) {", // Valid
                "int myMethod() {",                      // Invalid: not void
                "void method(abc) {",                 // Invalid: invalid parameter name
                "void method {"                          // Invalid: missing parentheses
        };

//        System.out.println("\nTesting Method Declaration Format:");
//        for (String testCase : methodDeclarationTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchMethodDeclarationFormat(testCase));
//        }

        // Test cases for return statement format
        String[] returnTestCases = {
                "return;",
                " return ; ",
                "return  ;",
                "return",
                "return something;",
                "return ; extra"
        };

//        System.out.println("\nTesting Return Statement Format:");
//        for (String testCase : returnTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchReturnFormat(testCase));
//        }

        // Test cases for end-of-scope format
        String[] endOfScopeTestCases = {
                "}",          // Valid
                " } ",        // Valid: extra spaces
                "   }",       // Valid: spaces before
                "}",          // Valid
                "} extra",    // Invalid: additional content
                "{",          // Invalid: opening brace
                "",           // Invalid: empty line
                "// }",       // Invalid: comment line
        };

//        System.out.println("\nTesting End-of-Scope Format:");
//        for (String testCase : endOfScopeTestCases) {
//            System.out.println("Testing: " + testCase);
//            System.out.println("Matches: " + matchEndOfScopeFormat(testCase));
//        }

        // Test cases for if/while format
        String[] ifWhileTestCases = {
                "if (true) {",
                "while (false) {",
                "if (x) {",
                "while (5.2) {",
                "if (true && x || 5) {",
                "if (true &&) {",
                "if () {",
                "if (a && b || ) {",
                "if ((x) && y) {",
                "while true) {",
                "if (true) {}",
        };

        System.out.println("\nTesting If/While Format:");
        for (String testCase : ifWhileTestCases) {
            System.out.println("Testing: " + testCase);
            System.out.println("Matches: " + matchIfWhileFormat(testCase));
        }

        // Test cases for assignment format
        String[] assignmentTestCases = {
                "x = 5;",                  // Valid
                "x = y;",                  // Valid
                "x = true;",               // Valid
                "x = \"hello\";",          // Valid
                "x = 'c';",                // Valid
                "x = 5, y = 10;",          // Valid: multiple assignments
                "  x  =  5 ,  y  = 10  ;", // Valid: extra spaces
                "x y = 5;",                // Invalid: missing comma
                "x = 5, = 10;",            // Invalid: missing identifier
                "x =;",                    // Invalid: missing value
                "= 5;",                    // Invalid: missing identifier
                "x = 5"                    // Invalid: missing semicolon
        };

        System.out.println("\nTesting Assignment Format:");
        for (String testCase : assignmentTestCases) {
            System.out.println("Testing: " + testCase);
            System.out.println("Matches: " + matchAssignmentFormat(testCase));
        }

        // Test cases for variable declaration format
        String[] declarationTestCases = {
                "int x;",                      // Valid: simple declaration
                "double x = 5.5;",             // Valid: initialized declaration
                "final boolean flag = true;",  // Valid: final declaration
                "char a, b = 'c';",            // Valid: multiple declarations
                "String s = \"hello\", t;",    // Valid: mixed initialization
                "int 123x;",                   // Invalid: variable starts with digit
                "float x;",                    // Invalid: unsupported type
                "boolean a = false, = true;",  // Invalid: missing variable name
                "int a, b,;",                  // Invalid: trailing comma
                "final x;",                    // Invalid: missing type
        };

        System.out.println("\nTesting Declaration Format:");
        for (String testCase : declarationTestCases) {
            System.out.println("Testing: " + testCase);
            System.out.println("Matches: " + matchDeclarationFormat(testCase));
        }
    }
}
