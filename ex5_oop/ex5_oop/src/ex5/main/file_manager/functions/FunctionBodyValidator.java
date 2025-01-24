package ex5.main.file_manager.functions;

import ex5.main.Variable;
import ex5.main.file_manager.RowValidnessClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ex5.main.Sjavac.SYNTAX_ERROR_EXIT_CODE;

/**
 * Class: FunctionBodyValidator
 *
 * Responsible for validating the body of functions written in s-Java code.
 * Ensures proper syntax, valid method structure, and correct usage of variables,
 * assignments, and control structures.
 */
public class FunctionBodyValidator {

    private static final String METHOD_CALL_FORMAT = "[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(([^\\)\\(]*)\\)\\s*;";
    private static final String INVALID_METHOD_CALL = "Invalid method call: ";
    private static final String EXCEPTION_UNDEFINED_METHOD = "Undefined method: ";
    private static final String REGEX_WORD_WORD = "\\s*,\\s*";
    private static final char CHARACTER_CLOSING_REGULAR_BRACE = ')';
    private static final String DUPLICATE_VARIABLE_NAME_IN_LOCAL_SCOPE =
            "Duplicate variable name in local scope: ";
    private static final String REGEX_WORD_SPACE_WORD = "\\s*=\\s*";
    private static final String INVALID_VARIABLE_DECLARATION_EXCEPTION = "Invalid variable declaration: ";
    private static final String FINAL_KEY_WORD = "final";
    private static final String SPLIT_FORMAT = "\\s+";
    private static final int SPLIT_LIMIT = 3;
    private static final int INT1 = 1;
    private static final int INT2 = 2;
    // Regex for variable declaration (with or without initialization)
    private static final String VALID_TYPES = "int|double|boolean|char|String";
    private static final String VARIABLE_NAME_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final String VALUE_PATTERN = ".*"; // Placeholder for further validation
    private static final String DECLERATION_PATTERN = String.format(
            "^(final\\s+)?(%s)\\s+(%s(\\s*=\\s*%s)?(\\s*,\\s*%s(\\s*=\\s*%s)?)*)\\s*;$",
            VALID_TYPES, VARIABLE_NAME_PATTERN, VALUE_PATTERN, VARIABLE_NAME_PATTERN, VALUE_PATTERN
    );

    private static final char CHAR_OPENING_REGULAR_BRACE = '(';
    private static final String METHOD_NOT_FOUND_IN_FUNCTIONS_MAP = "Method not found in functionsMap: ";
    private static final String CLOSING_CURLY_BARCE = "}";
    private static final String CLOSING_CURLY_BRACE = "{";
    private static final String INVALID_METHOD_DECLARATION = "Invalid method declaration: ";
    private static final String METHOD_DECLERATION_PATTERN =
            "void\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(.*\\)\\s*\\{";
    private static final String UNMATCHED_CLOSING_BRACE = "Unmatched closing brace.";
    private static final String IF = "if";
    private static final String WHILE = "while";
    private static final String RETURN = "return";
    private static final String UNMATCHED_OPENING_BRACE = "Unmatched opening brace.";
    private static final String RETURN_AND_END_LINE = "return;";
    private static final String MISSING_RETURN_STATEMENT_AT_THE_END_OF_THE_METHOD =
            "Missing return statement at the end of the method.";
    private static final String CONDITIONAL_PATTERN = "^(if|while)\\s*\\(([^\\)]+)\\)\\s*\\{$";
    private static final String INVALID_CONDITIONAL_BLOCK = "Invalid conditional block: ";
    private static final String UNMATCHED_OPENING_BRACE_FOR_CONDITIONAL_BLOCK =
            "Unmatched opening brace for conditional block.";
    private static final String SPLIT_PATTERN_FOR_VALIDATE_CONDITION = "\\|\\||&&";
    private static final String EMPTY_CONDITION_ECXEPTION = "Empty condition in: ";
    private static final String INVALID_VARIABLE_TYPE_EXCEPTION = "Invalid variable type in condition: ";
    private static final String UNKNOWN_VARIABLE_OR_INVALID_LITERAL_IN_CONDITION_EXCEPTION =
            "Unknown variable or invalid literal in condition: ";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String BOOLEAN = "boolean";
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String INT_DOUBLE_BOOLEAN_CHAR_STRING = "(int|double|boolean|char|String).*";
    private static final String INVALID_LINE = "Invalid line: ";
    private static final String SENICOLON = ";";
    private static final String EMPTY = "";
    private static final String UNDEFINED_VARIABLE = "Undefined variable: ";
    private static final String TYPE_MISMATCH_FOR_VARIABLE = "Type mismatch for variable: ";
    private static final String CANNOT_ASSIGN_A_VALUE_TO_FINAL_VARIABLE =
            "Cannot assign a value to final variable: ";
    private static final String INT_PATTERN = "-?\\d+";
    private static final String DOUBLE_PATTERN = "-?\\d*\\.\\d+|-?\\d+\\.\\d*";
    private static final String TRUE_FALSE = "true|false";
    private static final String CHAR_PATTERN = "'.'";
    private static final String STRING_PATTERN = "\"[^\"]*\"";
    private static final String INVALID_LITERAL_VALUE = "Invalid literal value: ";
    private static final String INVALID_RETURN_STATEMENT = "Invalid return statement: ";
    private static final String CHAR = "char";
    private static final String STRING = "String";
    private static final String UNKNOWN_TYPE = "Unknown type: ";
//    cllass variables
    private final List<String> linesArray;
    private final HashMap<String, Variable<?>> globalMap;
    private final HashMap<String, List<Map<String, Variable<Object>>>> functionsMap;

    /**
     * Constructor for FunctionBodyValidator
     *
     * @param linesArray   A list of lines representing the function body.
     * @param globalMap    A map of global variables.
     * @param functionsMap A map of all defined functions and their parameters.
     */
    public FunctionBodyValidator(List<String> linesArray,
                                 HashMap<String, Variable<?>> globalMap,
                                 HashMap<String, List<Map<String, Variable<Object>>>> functionsMap) {
        this.linesArray = linesArray;
        this.globalMap = globalMap;
        this.functionsMap = functionsMap;
    }

    /**
     * Processes all methods in the lines array and validates their structure.
     */
    public void processAllMethods() {
        int currentLine = 0;
        while (currentLine < linesArray.size()) {
            String line = linesArray.get(currentLine).trim();
            // Detect method declaration
            if (RowValidnessClass.isStartFunction(line)) {
                // Extract the method lines
                List<String> methodLines = extractMethod(currentLine);
                currentLine += methodLines.size(); // Skip processed method lines
                // Extract method name from the declaration
                String methodName = extractMethodName(line);
                // Validate the method
                validateMethod(methodLines, methodName);
            }
            else{
                currentLine++;
            }
        }
    }

    private String extractMethodName(String declarationLine) {
        // Regex to match the method declaration: void methodName(...)
        String methodPattern = METHOD_DECLERATION_PATTERN;
        Pattern pattern = Pattern.compile(methodPattern);
        Matcher matcher = pattern.matcher(declarationLine);

        if (matcher.matches()) {
            return matcher.group(INT1); // Return the captured method name
        }
        // This shouldn't happen if prior validation is correct
        throw new IllegalStateException(INVALID_METHOD_DECLARATION + declarationLine);
    }

    private List<String> extractMethod(int startIndex) {
        List<String> methodLines = new ArrayList<>();
        int braceBalance = 0;
        boolean started = false;

        for (int i = startIndex; i < linesArray.size(); i++) {
            String line = linesArray.get(i).trim();
            methodLines.add(line);
            if (line.contains(CLOSING_CURLY_BRACE)) {
                braceBalance++;
                started = true;
            }
            if (line.contains(CLOSING_CURLY_BARCE)) {
                braceBalance--;
            }
            if (started && braceBalance == 0) {
                return methodLines;
            }
        }
        return methodLines;
    }

    /**
     * Validates the content of a single method in s-Java code.
     *
     * @param methodLines List of code lines representing the method.
     * @param methodName  The name of the method being validated.
     */
    public void validateMethod(List<String> methodLines, String methodName) {
        try {
            // Step 1: Initialize local variable map
            Map<String, Variable<?>> localVariables = new HashMap<>();
            // Add method parameters from functionsMap to localVariables
            addMethodParametersToLocalVariables(methodName, localVariables);

            // Step 2: Validate Method Body
            validateMethodBody(methodLines.subList(INT1, methodLines.size() - INT1), localVariables,
                    new HashMap<>(), true);
            // Step 3: Ensure Method Ends with Valid Return
            validateReturnStatement(methodLines.get(methodLines.size() - INT2).trim());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(SYNTAX_ERROR_EXIT_CODE);
        }
    }

    private void addMethodParametersToLocalVariables(String methodName, Map<String,
            Variable<?>> localVariables) {
        // Retrieve the parameter list for the given method
        List<Map<String, Variable<Object>>> parameterList = functionsMap.get(methodName);

        if (parameterList == null) {
            throw new RuntimeException(METHOD_NOT_FOUND_IN_FUNCTIONS_MAP + methodName);
        }

        // Add each parameter to the localVariables map
        for (Map<String, Variable<Object>> parameterMap : parameterList) {
            for (Map.Entry<String, Variable<Object>> entry : parameterMap.entrySet()) {
                String paramName = entry.getKey();
                Variable<Object> paramVariable = entry.getValue();

                // Ensure no duplicate parameter names in local scope
                if (localVariables.containsKey(paramName)) {
                    throw new RuntimeException("Duplicate parameter name in method: " + methodName);
                }

                // Add parameter to localVariables
                localVariables.put(paramName, new Variable<>(null, paramVariable.getType(),
                        paramVariable.isFinal()));
            }
        }
    }

    private void validateMethodBody(List<String> bodyLines, Map<String, Variable<?>> localVariables,
                                    Map<String, Variable<?>> outerVariables,
                                    boolean expectReturn)
            throws RuntimeException {
        int braceBalance = 0;
        int currentLine = 0;
        String line;

        while (currentLine < bodyLines.size()) {
            line = bodyLines.get(currentLine).trim();

            if (line.contains(CLOSING_CURLY_BRACE)) braceBalance++;
            if (line.contains(CLOSING_CURLY_BARCE)) braceBalance--;

            if (braceBalance < 0) {
                throw new RuntimeException(UNMATCHED_CLOSING_BRACE);
            }

            if (line.startsWith(IF) || line.startsWith(WHILE)) {
                int conditionLines = validateConditionalBlock(line, bodyLines, localVariables);
                braceBalance--; // closing } for the conditional block checked in validateConditionalBlock
                currentLine += conditionLines;  // Skip processed block lines
            } else if (line.startsWith(RETURN)) {
                currentLine++;
            } else {
                validateMethodLine(line, localVariables, outerVariables);
                currentLine++;
            }
        }

        if (braceBalance != 0) {
            throw new RuntimeException(UNMATCHED_OPENING_BRACE);
        }

        // Check for a return statement if expected
        if (expectReturn) {
            String lastLine = bodyLines.get(bodyLines.size() - INT1).trim();
            if (!lastLine.equals(RETURN_AND_END_LINE)) {
                throw new RuntimeException(MISSING_RETURN_STATEMENT_AT_THE_END_OF_THE_METHOD);
            }
        }
    }


    private int validateConditionalBlock(String line, List<String> bodyLines, Map<String,
            Variable<?>> localVariables) {
        String conditionalPattern = CONDITIONAL_PATTERN;
        if (!line.matches(conditionalPattern)) {
            throw new RuntimeException(INVALID_CONDITIONAL_BLOCK + line);
        }

        // Extract and validate the condition expression
        String condition = line.substring(line.indexOf(CHAR_OPENING_REGULAR_BRACE) + INT1,
                line.indexOf(CHARACTER_CLOSING_REGULAR_BRACE)).trim();
        validateCondition(condition, localVariables, globalMap);

        int braceBalance = INT1;  // We start with 1 because the current line contains '{'
        int currentLine = bodyLines.indexOf(line) + INT1;  // Start processing from the next line

        // Create a new local scope by copying the existing variables
        Map<String, Variable<?>> outerVariables = new HashMap<>(localVariables);
        List<String> blockLines = new ArrayList<>();

        while (currentLine < bodyLines.size()) {
            String current = bodyLines.get(currentLine).trim();
            blockLines.add(current);

            // Handle braces
            if (current.contains(CLOSING_CURLY_BRACE)) braceBalance++;
            if (current.contains(CLOSING_CURLY_BARCE)) braceBalance--;

            // If we close the block, validate its contents recursively without requiring return
            if (braceBalance == 0) {
                blockLines.remove(blockLines.size() - INT1);
                validateMethodBody(blockLines, new HashMap<>(), outerVariables, false);
                return currentLine - bodyLines.indexOf(line) + INT1;  // Number of lines processed
            }

            currentLine++;
        }

        throw new RuntimeException(UNMATCHED_OPENING_BRACE_FOR_CONDITIONAL_BLOCK);
    }

    private void validateCondition(String condition, Map<String, Variable<?>> localVariables,
                                   Map<String, Variable<?>> globalVariables) {
        // Split condition by logical operators (|| or &&)
        String[] subConditions = condition.split(SPLIT_PATTERN_FOR_VALIDATE_CONDITION);

        for (String subCondition : subConditions) {
            subCondition = subCondition.trim();
            if (subCondition.isEmpty()) {
                throw new RuntimeException(EMPTY_CONDITION_ECXEPTION + condition);
            }

            // Check if the subCondition is a literal (boolean, int, or double)
            if (isBooleanLiteral(subCondition) || isNumericLiteral(subCondition)) {
                continue;
            }

            // Check if the subCondition is a valid variable
            if (localVariables.containsKey(subCondition)) {
                Variable<?> variable = localVariables.get(subCondition);
                if (!isValidConditionType(variable)) {
                    throw new RuntimeException(INVALID_VARIABLE_TYPE_EXCEPTION + subCondition);
                }
            } else if (globalVariables.containsKey(subCondition)) {
                Variable<?> variable = globalVariables.get(subCondition);
                if (!isValidConditionType(variable)) {
                    throw new RuntimeException(INVALID_VARIABLE_TYPE_EXCEPTION + subCondition);
                }
            } else {
                throw new RuntimeException(UNKNOWN_VARIABLE_OR_INVALID_LITERAL_IN_CONDITION_EXCEPTION +
                        subCondition);
            }
        }
    }


    // Utility method to check if a string is a boolean literal
    private boolean isBooleanLiteral(String value) {
        return TRUE.equals(value) || FALSE.equals(value);
    }

    // Utility method to check if a string is a numeric literal
    private boolean isNumericLiteral(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Utility method to check if a variable is of a valid type for a condition
    private boolean isValidConditionType(Variable<?> variable) {
        return BOOLEAN.equals(variable.getType()) || INT.equals(variable.getType()) ||
                DOUBLE.equals(variable.getType());
    }


    private void validateMethodLine(String line, Map<String, Variable<?>> localVariables, Map<String,
            Variable<?>> outerVariables) {
        if (line.matches(".*;")) { // todo maybe redundant
            if (RowValidnessClass.isInMethodAssignment(line)) {
                validateAssignment(line, localVariables, outerVariables);
            } else if (line.matches(INT_DOUBLE_BOOLEAN_CHAR_STRING)) {
                validateVariableDeclaration(line, localVariables);
            } else {
                validateMethodCall(line);
            }
        } else {
            throw new RuntimeException(INVALID_LINE + line);
        }
    }

    private void validateAssignment(String line, Map<String, Variable<?>> localVariables, Map<String,
            Variable<?>> outerVariables) {
        String[] parts = line.split(REGEX_WORD_SPACE_WORD);
        String variableName = parts[0].trim();
        String value = parts[INT1].replace(SENICOLON, EMPTY).trim();


        Variable<?> variable = localVariables.getOrDefault(
                variableName,
                outerVariables.getOrDefault(
                        variableName,
                        globalMap.get(variableName))
        );

        if (variable == null) {
            throw new RuntimeException(UNDEFINED_VARIABLE + variableName);
        }

        Object resolvedValue = resolveValue(value, localVariables, globalMap);

        // Dynamically check type compatibility
        if (!isTypeCompatible(variable.getType(), resolvedValue)) {
            throw new RuntimeException(TYPE_MISMATCH_FOR_VARIABLE + variableName);
        }

        if (variable.isFinal()) {
            throw new RuntimeException(CANNOT_ASSIGN_A_VALUE_TO_FINAL_VARIABLE + variableName);
        }
    }

    private Object resolveValue(String value, Map<String, Variable<?>> localVariables,
                                Map<String, Variable<?>> globalMap) {
        if (localVariables.containsKey(value)) {
            return localVariables.get(value).getValue();
        }
        if (globalMap.containsKey(value)) {
            return globalMap.get(value).getValue();
        }
        return validateLiteral(value); // Logic for literal validation
    }

    private Object validateLiteral(String value) {
        // Match against type-specific patterns
        if (value.matches(INT_PATTERN)) {
            return Integer.parseInt(value);
        }
        if (value.matches(DOUBLE_PATTERN)) {
            return Double.parseDouble(value);
        }
        if (value.matches(TRUE_FALSE)) {
            return Boolean.parseBoolean(value);
        }
        if (value.matches(CHAR_PATTERN)) {
            return value.charAt(INT1); // Extract character inside single quotes
        }
        if (value.matches(STRING_PATTERN)) {
            return value.substring(INT1, value.length() - INT1); // Remove quotes
        }

        throw new RuntimeException(INVALID_LITERAL_VALUE + value);
    }

    private void validateReturnStatement(String line) {
        if (!line.equals(RETURN_AND_END_LINE)) {
            throw new RuntimeException(INVALID_RETURN_STATEMENT + line);
        }
    }

    private boolean isTypeCompatible(String targetType, Object resolvedValue) {
        if (resolvedValue == null) {
            return true; // Null is compatible with all types
        }

        switch (targetType) {
            case INT:
                return resolvedValue instanceof Integer;
            case DOUBLE:
                return resolvedValue instanceof Double || resolvedValue instanceof Integer;
                // int can be assigned to double
            case BOOLEAN:
                return resolvedValue instanceof Boolean || resolvedValue instanceof Integer ||
                        resolvedValue instanceof Double;
            case CHAR:
                return resolvedValue instanceof Character;
            case STRING:
                return resolvedValue instanceof String;
            default:
                throw new RuntimeException(UNKNOWN_TYPE + targetType);
        }
    }

    private void validateVariableDeclaration(String line, Map<String, Variable<?>> localVariables) {


        if (!line.matches(DECLERATION_PATTERN)) {
            throw new RuntimeException(INVALID_VARIABLE_DECLARATION_EXCEPTION + line);
        }

        // Extract components
        String[] parts = line.split(SPLIT_FORMAT, SPLIT_LIMIT);
        boolean isFinal = parts[0].equals(FINAL_KEY_WORD);
        String type = isFinal ? parts[INT1] : parts[0];
        String variables = isFinal ? parts[INT2] : parts[INT1];

        // Handle multiple variables separated by commas
        String[] declarations = variables.split(REGEX_WORD_WORD);
        for (String declaration : declarations) {
            String[] nameValue = declaration.split(REGEX_WORD_SPACE_WORD);
            String name = nameValue[0];

            if (localVariables.containsKey(name)) {
                throw new RuntimeException(DUPLICATE_VARIABLE_NAME_IN_LOCAL_SCOPE + name);
            }

            Object resolvedValue = null;
            if (nameValue.length > INT1) {
                resolvedValue = resolveValue(nameValue[INT1], localVariables, null);
            }

            localVariables.put(name, new Variable<>(resolvedValue, type, isFinal));
        }
    }

    private void validateMethodCall(String line) {
        // Regex for method call: methodName(arg1, arg2, ...)
        String methodCallPattern = METHOD_CALL_FORMAT;
        if (!line.matches(methodCallPattern)) {
            throw new RuntimeException(INVALID_METHOD_CALL + line);
        }

        String methodName = line.substring(0, line.indexOf(CHAR_OPENING_REGULAR_BRACE)).trim();
        if (!functionsMap.containsKey(methodName)) {
            throw new RuntimeException(EXCEPTION_UNDEFINED_METHOD + methodName);
        }

        String arguments = line.substring(line.indexOf(CHAR_OPENING_REGULAR_BRACE) + INT1,
                line.indexOf(CHARACTER_CLOSING_REGULAR_BRACE)).trim();
        if (!arguments.isEmpty()) {
            String[] args = arguments.split(REGEX_WORD_WORD);
            for (String arg : args) {
                resolveValue(arg, new HashMap<>(), new HashMap<>());
            }
        }
    }
}
