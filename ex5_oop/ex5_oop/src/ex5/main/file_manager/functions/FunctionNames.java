package ex5.main.file_manager.functions;

import ex5.main.Variable;
import ex5.main.file_manager.RowValidnessClass;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The FunctionNames class extracts and validates function declarations from s-Java code.
 * It processes function headers, checks for syntax validity, and stores function details.
 */
public class FunctionNames {

    private static final String ERROR_INVALID_DECLARATION = "Invalid function declaration:" +
            " Missing parentheses.";
    private static final String ERROR_INVALID_SYNTAX = "Invalid function declaration syntax: ";
    private static final String ERROR_DUPLICATE_FUNCTION = "Duplicate function name: ";
    private static final String ERROR_INVALID_PARAMETER_SYNTAX = "Invalid function parameter syntax: ";
    private static final String ERROR_INVALID_PARAMETER = "Invalid parameter declaration: ";
    private static final String ERROR_INVALID_PARAMETER_TYPE = "Invalid parameter type : ";
    private static final String FUNCTION_DECLARATION_PATTERN = "^\\s*void\\s+([a-zA-Z]\\w*)\\s*$";
    private static final String PARAMETER_PATTERN = "^\\s*((\\s*(int|double|boolean|char|String)\\s+" +
            "[a-zA-Z]\\w*\\s*)(,\\s*(int|double|boolean|char|String)\\s+[a-zA-Z]\\w*\\s*)*)?\\)\\s*\\{\\s*$";
    private static final String SPLIT_PATTERN = "\\("; // Split into left and right parts at the first '('
    private static final String VAR_TYPES = "int|double|boolean|char|String";
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final String COMA_SEPEREOR_PATTERN = "\\s*,\\s*";
    private static final String SPACES_PATTERN = "\\s+";
    //    private fields
    private final int linesNumber;
    private final HashMap<String, List<Map<String, Variable<Object>>>> functionsMap = new HashMap<>();
    private final List<String> linesArray;

    /**
     * Constructor for FunctionNames.
     *
     * @param linesArray List of code lines.
     */
    public FunctionNames(List<String> linesArray) {
        this.linesArray = linesArray;
        this.linesNumber = linesArray.size();
    }

    /**
     * Extracts all function names and validates their declarations.
     *
     * @throws RuntimeException if a function declaration is invalid.
     */
    public void getAllFunctionsNames() throws RuntimeException {
        for (int index = 0; index < linesNumber; index++) {
            String line = linesArray.get(index);
            if (RowValidnessClass.isStartFunction(line)) {
                checkFunctionDeclaration(line);
            }
        }
    }

    /**
     * Retrieves the map of function names and their associated parameter lists.
     *
     * @return HashMap containing function names and parameter details.
     */
    public HashMap<String, List<Map<String, Variable<Object>>>> getFunctionsMap() {
        return functionsMap;
    }

    private void checkFunctionDeclaration(String line) throws RuntimeException {
        String[] splitted = line.split(SPLIT_PATTERN, TWO);
        if (splitted.length != TWO) {
            throw new RuntimeException(ERROR_INVALID_DECLARATION);
        }
        String functionLeftPart = splitted[0];
        String functionRightPart = splitted[ONE].trim();

        Pattern leftPattern = Pattern.compile(FUNCTION_DECLARATION_PATTERN);
        Matcher matcher = leftPattern.matcher(functionLeftPart);
        if (!matcher.matches()) {
            throw new RuntimeException(ERROR_INVALID_SYNTAX + line);
        }

        String functionName = matcher.group(ONE);

        if (functionsMap.containsKey(functionName)) {
            throw new RuntimeException(ERROR_DUPLICATE_FUNCTION + functionName);
        }

        Pattern paramPattern = Pattern.compile(PARAMETER_PATTERN);
        Matcher paramMatcher = paramPattern.matcher(functionRightPart);
        if (!paramMatcher.matches()) {
            throw new RuntimeException(ERROR_INVALID_PARAMETER_SYNTAX + line);
        }

        String paramList = paramMatcher.group(1);
        List<Map<String, Variable<Object>>> parameterList = new ArrayList<>();

        if (paramList != null && !paramList.isEmpty()) {
            String[] params = paramList.split(COMA_SEPEREOR_PATTERN);
            for (String param : params) {
                String[] paramParts = param.trim().split(SPACES_PATTERN);
                if (paramParts.length != TWO) {
                    throw new RuntimeException(ERROR_INVALID_PARAMETER + param);
                }

                String paramType = paramParts[0];

                String paramName = paramParts[ONE];
                if (!paramType.matches(VAR_TYPES)) {
                    throw new RuntimeException(ERROR_INVALID_PARAMETER_TYPE + paramType);
                }

                Variable<Object> variable = new Variable<>(null, paramType, false);
                Map<String, Variable<Object>> varMap = new HashMap<>();
                varMap.put(paramName, variable);
                parameterList.add(varMap);
            }
        }
        functionsMap.put(functionName, parameterList);
    }
}
