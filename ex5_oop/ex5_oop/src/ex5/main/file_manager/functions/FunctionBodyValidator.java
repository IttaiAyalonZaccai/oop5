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

public class FunctionBodyValidator {

    private final List<String> linesArray;
    private final HashMap<String, Variable<?>> globalMap;
    private final HashMap<String, List<Map<String, Variable<Object>>>> functionsMap;

    public FunctionBodyValidator(List<String> linesArray,
                                 HashMap<String, Variable<?>> globalMap,
                                 HashMap<String, List<Map<String, Variable<Object>>>> functionsMap) {
        this.linesArray = linesArray;
        this.globalMap = globalMap;
        this.functionsMap = functionsMap;
    }

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
        String methodPattern = "void\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\(.*\\)\\s*\\{";
        Pattern pattern = Pattern.compile(methodPattern);
        Matcher matcher = pattern.matcher(declarationLine);

        if (matcher.matches()) {
            return matcher.group(1); // Return the captured method name
        }
        // This shouldn't happen if prior validation is correct
        throw new IllegalStateException("Invalid method declaration: " + declarationLine);
    }

    private List<String> extractMethod(int startIndex) {
        List<String> methodLines = new ArrayList<>();
        int braceBalance = 0;
        boolean started = false;

        for (int i = startIndex; i < linesArray.size(); i++) {
            String line = linesArray.get(i).trim();
            methodLines.add(line);
            if (line.contains("{")) {
                braceBalance++;
                started = true;
            }
            if (line.contains("}")) {
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
     * @param methodLines        List of code lines representing the method.
     * @return                   0 if the method is valid, 1 if invalid.
     */
    public void validateMethod(List<String> methodLines, String methodName) {
        try {
            // Step 1: Initialize local variable map
            Map<String, Variable<?>> localVariables = new HashMap<>();
            // Add method parameters from functionsMap to localVariables
            addMethodParametersToLocalVariables(methodName, localVariables);

            // Step 2: Validate Method Body
            validateMethodBody(methodLines.subList(1, methodLines.size() - 1), localVariables,
                    new HashMap<>(), true);
            // Step 3: Ensure Method Ends with Valid Return
            validateReturnStatement(methodLines.get(methodLines.size() - 2).trim());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(SYNTAX_ERROR_EXIT_CODE);
        }
    }

    private void addMethodParametersToLocalVariables(String methodName, Map<String, Variable<?>> localVariables) {
        // Retrieve the parameter list for the given method
        List<Map<String, Variable<Object>>> parameterList = functionsMap.get(methodName);

        if (parameterList == null) {
            throw new RuntimeException("Method not found in functionsMap: " + methodName);
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
                localVariables.put(paramName, new Variable<>(null, paramVariable.getType(), paramVariable.isFinal()));
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

            if (line.contains("{")) braceBalance++;
            if (line.contains("}")) braceBalance--;

            if (braceBalance < 0) {
                throw new RuntimeException("Unmatched closing brace.");
            }

            if (line.startsWith("if") || line.startsWith("while")) {
                int conditionLines = validateConditionalBlock(line, bodyLines, localVariables);
                braceBalance--; // closing } for the conditional block checked in validateConditionalBlock
                currentLine += conditionLines;  // Skip processed block lines
            } else if (line.startsWith("return")) {
                currentLine++;
            } else {
                validateMethodLine(line, localVariables, outerVariables);
                currentLine++;
            }
        }

        if (braceBalance != 0) {
            throw new RuntimeException("Unmatched opening brace.");
        }

        // Check for a return statement if expected
        if (expectReturn) {
            String lastLine = bodyLines.get(bodyLines.size() - 1).trim();
            if (!lastLine.equals("return;")) {
                throw new RuntimeException("Missing return statement at the end of the method.");
            }
        }
    }


    private int validateConditionalBlock(String line, List<String> bodyLines, Map<String, Variable<?>> localVariables) {
        String conditionalPattern = "^(if|while)\\s*\\(([^\\)]+)\\)\\s*\\{$";
        if (!line.matches(conditionalPattern)) {
            throw new RuntimeException("Invalid conditional block: " + line);
        }

        // Extract and validate the condition expression
        String condition = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        validateCondition(condition, localVariables, globalMap);

        int braceBalance = 1;  // We start with 1 because the current line contains '{'
        int currentLine = bodyLines.indexOf(line) + 1;  // Start processing from the next line

        // Create a new local scope by copying the existing variables
        Map<String, Variable<?>> outerVariables = new HashMap<>(localVariables);
        List<String> blockLines = new ArrayList<>();

        while (currentLine < bodyLines.size()) {
            String current = bodyLines.get(currentLine).trim();
            blockLines.add(current);

            // Handle braces
            if (current.contains("{")) braceBalance++;
            if (current.contains("}")) braceBalance--;

            // If we close the block, validate its contents recursively without requiring return
            if (braceBalance == 0) {
                blockLines.remove(blockLines.size() - 1);
                validateMethodBody(blockLines, new HashMap<>(), outerVariables, false);
                return currentLine - bodyLines.indexOf(line) + 1;  // Number of lines processed
            }

            currentLine++;
        }

        throw new RuntimeException("Unmatched opening brace for conditional block.");
    }

    private void validateCondition(String condition, Map<String, Variable<?>> localVariables, Map<String, Variable<?>> globalVariables) {
        // Split condition by logical operators (|| or &&)
        String[] subConditions = condition.split("\\|\\||&&");

        for (String subCondition : subConditions) {
            subCondition = subCondition.trim();
            if (subCondition.isEmpty()) {
                throw new RuntimeException("Empty condition in: " + condition);
            }

            // Check if the subCondition is a literal (boolean, int, or double)
            if (isBooleanLiteral(subCondition) || isNumericLiteral(subCondition)) {
                continue;
            }

            // Check if the subCondition is a valid variable
            if (localVariables.containsKey(subCondition)) {
                Variable<?> variable = localVariables.get(subCondition);
                if (!isValidConditionType(variable)) {
                    throw new RuntimeException("Invalid variable type in condition: " + subCondition);
                }
            } else if (globalVariables.containsKey(subCondition)) {
                Variable<?> variable = globalVariables.get(subCondition);
                if (!isValidConditionType(variable)) {
                    throw new RuntimeException("Invalid variable type in condition: " + subCondition);
                }
            } else {
                throw new RuntimeException("Unknown variable or invalid literal in condition: " + subCondition);
            }
        }
    }


    // Utility method to check if a string is a boolean literal
    private boolean isBooleanLiteral(String value) {
        return "true".equals(value) || "false".equals(value);
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
        return "boolean".equals(variable.getType()) || "int".equals(variable.getType()) || "double".equals(variable.getType());
    }


    private void validateMethodLine(String line, Map<String, Variable<?>> localVariables, Map<String,
            Variable<?>> outerVariables) {
        if (line.matches(".*;")) { // todo maybe redundant
            if (RowValidnessClass.isInMethodAssignment(line)) {
                validateAssignment(line, localVariables, outerVariables);
            } else if (line.matches("(int|double|boolean|char|String).*")) {
                validateVariableDeclaration(line, localVariables);
            } else {
                validateMethodCall(line);
            }
        } else {
            throw new RuntimeException("Invalid line: " + line);
        }
    }

    private void validateAssignment(String line, Map<String, Variable<?>> localVariables, Map<String,
            Variable<?>> outerVariables) {
        String[] parts = line.split("\\s*=\\s*");
        String variableName = parts[0].trim();
        String value = parts[1].replace(";", "").trim();


        Variable<?> variable = localVariables.getOrDefault(
                variableName,
                outerVariables.getOrDefault(
                        variableName,
                        globalMap.get(variableName))
        );

        if (variable == null) {
            throw new RuntimeException("Undefined variable: " + variableName);
        }

        Object resolvedValue = resolveValue(value, localVariables, globalMap);

        // Dynamically check type compatibility
        if (!isTypeCompatible(variable.getType(), resolvedValue)) {
            throw new RuntimeException("Type mismatch for variable: " + variableName);
        }

        if (variable.isFinal()) {
            throw new RuntimeException("Cannot assign a value to final variable: " + variableName);
        }
    }

    private Object resolveValue(String value, Map<String, Variable<?>> localVariables, Map<String, Variable<?>> globalMap) {
        if (localVariables.containsKey(value)) {
            return localVariables.get(value).getValue();
        }
        if (globalMap.containsKey(value)) {
            return globalMap.get(value).getValue();
        }
        return validateLiteral(value); // Logic for literal validation
    }

    private Object validateLiteral(String value) {
        // Patterns for literals
        String intPattern = "-?\\d+";
        String doublePattern = "-?\\d*\\.\\d+|-?\\d+\\.\\d*";
        String booleanPattern = "true|false";
        String charPattern = "'.'";
        String stringPattern = "\"[^\"]*\"";

        // Match against type-specific patterns
        if (value.matches(intPattern)) {
            return Integer.parseInt(value);
        }
        if (value.matches(doublePattern)) {
            return Double.parseDouble(value);
        }
        if (value.matches(booleanPattern)) {
            return Boolean.parseBoolean(value);
        }
        if (value.matches(charPattern)) {
            return value.charAt(1); // Extract character inside single quotes
        }
        if (value.matches(stringPattern)) {
            return value.substring(1, value.length() - 1); // Remove quotes
        }

        throw new RuntimeException("Invalid literal value: " + value);
    }

    private void validateReturnStatement(String line) {
        if (!line.equals("return;")) {
            throw new RuntimeException("Invalid return statement: " + line);
        }
    }

    private boolean isTypeCompatible(String targetType, Object resolvedValue) {
        if (resolvedValue == null) {
            return true; // Null is compatible with all types
        }

        switch (targetType) {
            case "int":
                return resolvedValue instanceof Integer;
            case "double":
                return resolvedValue instanceof Double || resolvedValue instanceof Integer; // int can be assigned to double
            case "boolean":
                return resolvedValue instanceof Boolean || resolvedValue instanceof Integer || resolvedValue instanceof Double;
            case "char":
                return resolvedValue instanceof Character;
            case "String":
                return resolvedValue instanceof String;
            default:
                throw new RuntimeException("Unknown type: " + targetType);
        }
    }

    private void validateVariableDeclaration(String line, Map<String, Variable<?>> localVariables) {
        // Regex for variable declaration (with or without initialization)
        String validTypes = "int|double|boolean|char|String";
        String variableNamePattern = "[a-zA-Z_][a-zA-Z0-9_]*";
        String valuePattern = ".*"; // Placeholder for further validation
        String declarationPattern = String.format(
                "^(final\\s+)?(%s)\\s+(%s(\\s*=\\s*%s)?(\\s*,\\s*%s(\\s*=\\s*%s)?)*)\\s*;$",
                validTypes, variableNamePattern, valuePattern, variableNamePattern, valuePattern
        );

        if (!line.matches(declarationPattern)) {
            throw new RuntimeException("Invalid variable declaration: " + line);
        }

        // Extract components
        String[] parts = line.split("\\s+", 3);
        boolean isFinal = parts[0].equals("final");
        String type = isFinal ? parts[1] : parts[0];
        String variables = isFinal ? parts[2] : parts[1];

        // Handle multiple variables separated by commas
        String[] declarations = variables.split("\\s*,\\s*");
        for (String declaration : declarations) {
            String[] nameValue = declaration.split("\\s*=\\s*");
            String name = nameValue[0];

            if (localVariables.containsKey(name)) {
                throw new RuntimeException("Duplicate variable name in local scope: " + name);
            }

            Object resolvedValue = null;
            if (nameValue.length > 1) {
                resolvedValue = resolveValue(nameValue[1], localVariables, null);
            }

            localVariables.put(name, new Variable<>(resolvedValue, type, isFinal));
        }
    }

    private void validateMethodCall(String line) {
        // Regex for method call: methodName(arg1, arg2, ...)
        String methodCallPattern = "[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(([^\\)\\(]*)\\)\\s*;";
        if (!line.matches(methodCallPattern)) {
            throw new RuntimeException("Invalid method call: " + line);
        }

        String methodName = line.substring(0, line.indexOf('(')).trim();
        if (!functionsMap.containsKey(methodName)) {
            throw new RuntimeException("Undefined method: " + methodName);
        }

        String arguments = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
        if (!arguments.isEmpty()) {
            String[] args = arguments.split("\\s*,\\s*");
            for (String arg : args) {
                // Validate each argument (assumes resolveValue validates types)
                resolveValue(arg, new HashMap<>(), new HashMap<>());
            }
        }
    }
}
