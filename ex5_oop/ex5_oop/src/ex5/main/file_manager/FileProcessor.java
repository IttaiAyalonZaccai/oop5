package ex5.main.file_manager;

import ex5.main.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ex5.main.Sjavac.SYNTAX_ERROR_EXIT_CODE;
/**
 * Processes the lines of a file, checks for line validity,
 * and handles comments and blank lines.
 */
public class FileProcessor{
//    constants
    private final List<String> linesArray;
    private final String VALID_TYPES = "int|double|boolean|char|String";

    private List<Integer> ignoredLinesIndexes = new ArrayList<>();
    private int linesNumber = 0;
    private HashMap<String, Variable<?>> globalMap = new HashMap<>();
    private HashMap<String, List<Map<String, Variable<Object>>>> functionsMap = new HashMap<>();

    /**
     * Constructs a FileProcessor object.
     *
     * @param bufferedReader A BufferedReader instance used to read the lines of the file.
     * @throws IOException If an I/O error occurs during file reading.
     */
    public FileProcessor(BufferedReader bufferedReader) throws IOException {
        this.linesArray = new ArrayList<String>();
        String line;

        while((line = bufferedReader.readLine()) != null) {
            linesNumber++;
            linesArray.add(line);
        }

        // remove empty or comment lines
        Pattern pattern = Pattern.compile("//.*|\\s*");
        linesArray.removeIf(line1 -> pattern.matcher(line1).matches());
        linesNumber = linesArray.size();
        checkLineValidity();
    }

    /**
     * Retrieves the line at the specified index.
     *
     * @param index The index of the line to retrieve.
     * @return The line at the specified index.
     */
    public String getLine(int index) {
        return linesArray.get(index);
    }

    /**
     * Checks the validity of all lines in the file.
     *
     * @throws IOException If an I/O error occurs during the process.
     */
    public void checkLineValidity() throws IOException {
        String line;
        for (int lineIndex = 0; lineIndex < linesNumber; lineIndex++) {
            line = linesArray.get(lineIndex);
            try {
                RowValidnessClass.check_suffixes(line, lineIndex);
                RowValidnessClass.checkMiddleComments(line, lineIndex);
//                checkIfLineIsCommentOrBlank(line, lineIndex);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                System.exit(SYNTAX_ERROR_EXIT_CODE);
            }
        }
    }

    public void validAndCreateGlobalMap() throws RuntimeException {
        // todo stack of { for validation that it is indeed a global var
        // todo what do to with final?
        // counter for tracking scope (stack-like)
        int scopeLevel = 0;
        // iterate over each line in the linesArray
        for (String line : linesArray) {
            // trim the line to remove leading/trailing whitespace
            // todo valid this line
            line = line.trim();
            // Check for opening brace
            if (line.endsWith("{")) {
                scopeLevel++;
                continue;
            }
            // check for closing brace
            if (line.endsWith("}")) {
                if (scopeLevel > 0) {
                    scopeLevel--;
                } else {
                    throw new RuntimeException("Unmatched closing brace encountered.");
                }
                continue;
            }
            // if at the global scope (scopeLevel == 0), validate the line
            if (scopeLevel == 0) {
                validateAndAddGlobalVariable(line);
            }
        }
        // after processing, ensure all braces are balanced
        if (scopeLevel != 0) {
            throw new RuntimeException("Unmatched opening brace(s) detected.");
        }
        System.out.println();
    }

    private void validateAndAddGlobalVariable(String line) throws RuntimeException {
        // Trim whitespace for cleaner processing
        line = line.trim();

        // Regex patterns for variable declaration and validation
        String validTypes = "int|double|boolean|char|String";
        String variableNamePattern = "[a-zA-Z_][a-zA-Z0-9_]*"; // Matches legal variable names
        String valuePattern = ".*"; // Placeholder for further value validation

        // Pattern for a global variable declaration
        String declarationPattern = String.format(
                "^(final\\s+)?(%s)\\s+(%s(\\s*=\\s*%s)?(\\s*,\\s*%s(\\s*=\\s*%s)?)*)\\s*;$",
                validTypes, variableNamePattern, valuePattern, variableNamePattern, valuePattern
        );

        // Compile the pattern
        Pattern pattern = Pattern.compile(declarationPattern);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid global variable declaration: " + line);
        }

        // Extract components
        boolean isFinal = matcher.group(1) != null; // Checks if "final" is present
        String type = matcher.group(2);
        String variables = matcher.group(3);

        // Split multiple variables
        String[] variableParts = variables.split("\\s*,\\s*");
        for (String varPart : variableParts) {
            // Parse variable name and value
            String[] nameValue = varPart.split("\\s*=\\s*");
            String name = nameValue[0];
            String value = nameValue.length > 1 ? nameValue[1] : null;

            // Validate name uniqueness
            if (globalMap.containsKey(name)) {
                throw new RuntimeException("Duplicate global variable name: " + name);
            }

            // Validate the value
            Object resolvedValue = null; // This will hold the parsed or resolved value
            if (value != null) {
                resolvedValue = validateValue(type, value);
            }

            // Add the variable to the map
            Variable<Object> variable = new Variable<>(resolvedValue, type, isFinal);
            globalMap.put(name, variable);
        }
    }

    private Object validateValue(String type, String value) throws RuntimeException {
        // If value is a reference to another variable
        if (globalMap.containsKey(value)) {
            Variable<?> sourceVar = globalMap.get(value);

            // Check type compatibility
            if (!isTypeCompatible(type, sourceVar.getType())) {
                throw new RuntimeException("Type mismatch: Cannot assign " + sourceVar.getType() + " to " + type);
            }

            // Return the source variable's value
            return sourceVar.getValue();
        }

        // Match value against type-specific patterns
        switch (type) {
            case "int":
                if (value.matches("-?\\d+")) {
                    return Integer.parseInt(value);
                }
                throw new RuntimeException("Invalid value for type int: " + value);

            case "double":
                if (value.matches("-?\\d*\\.\\d+|-?\\d+\\.\\d*|-?\\d+")) {
                    return Double.parseDouble(value);
                }
                throw new RuntimeException("Invalid value for type double: " + value);

            case "boolean":
                if (value.matches("true|false")) {
                    return Boolean.parseBoolean(value);
                }
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    return Double.parseDouble(value) != 0; // Non-zero values are true
                }
                throw new RuntimeException("Invalid value for type boolean: " + value);

            case "char":
                if (value.matches("'.'")) {
                    return value.charAt(1); // Extract the character inside single quotes
                }
                throw new RuntimeException("Invalid value for type char: " + value);

            case "String":
                if (value.matches("\"[^\"]*\"")) {
                    return value.substring(1, value.length() - 1); // Remove the quotes
                }
                throw new RuntimeException("Invalid value for type String: " + value);

            default:
                throw new RuntimeException("Unknown type: " + type);
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
            validateMethodBody(methodLines.subList(1, methodLines.size() - 1), localVariables);
            System.out.println();
            // Step 3: Ensure Method Ends with Valid Return
//            validateReturnStatement(methodLines.get(methodLines.size() - 1).trim());
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



    private void extractMethodParameters(String signature, Map<String, Variable<?>> localVariables) {
        String paramsPart = signature.substring(signature.indexOf('(') + 1, signature.indexOf(')')).trim();
        if (paramsPart.isEmpty()) return;

        String[] params = paramsPart.split("\\s*,\\s*");
        for (String param : params) {
            String[] parts = param.trim().split("\\s+");
            boolean isFinal = parts.length == 3 && parts[0].equals("final");
            String type = parts[isFinal ? 1 : 0];
            String name = parts[isFinal ? 2 : 1];

            // Add parameter to local variables
            if (localVariables.containsKey(name)) { // todo i think this is not good...
                throw new RuntimeException("Duplicate parameter name: " + name);
            }
            localVariables.put(name, new Variable<>(null, type, isFinal));
        }
    }

    private void validateMethodBody(List<String> bodyLines, Map<String, Variable<?>> localVariables)
            throws RuntimeException{
        int braceBalance = 0;

        for (String line : bodyLines) {
            line = line.trim();

            // Handle braces
            if (line.contains("{"))
                braceBalance++;
            if (line.contains("}"))
                braceBalance--;

            if (braceBalance < 0) {
                throw new RuntimeException("Unmatched closing brace.");
            }

            // Validate line content
//            todo
            if (line.startsWith("if") || line.startsWith("while")) {
//                validateConditionalBlock(line, bodyLines, localVariables); // todo check later
                continue;
            } else if (line.startsWith("return")) {
                // Ensure return is handled at the end of the method todo?
                continue;
            } else {
                validateLine(line, localVariables);
            }
        }

        if (braceBalance != 0) {
            throw new RuntimeException("Unmatched opening brace.");
        }
    }

    private void validateConditionalBlock(String line, List<String> bodyLines, Map<String, Variable<?>> localVariables) {
        String conditionalPattern = "(if|while)\\s*\\(([^)]+)\\)\\s*\\{";
        if (!line.matches(conditionalPattern)) {
            throw new RuntimeException("Invalid conditional block: " + line);
        }
        String condition = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
//        validateCondition(condition, localVariables, globalMap); todo un-comment

        // Nested blocks validation would happen recursively in validateMethodBody
    }

    private void validateLine(String line, Map<String, Variable<?>> localVariables) {
        if (line.matches(".*;")) { // todo maybe redundant
            // todo maybe need to add validation for line valid structure
            if (RowValidnessClass.isInMethodAssignment()) { // todo, change to: valid it is a validassignment from here
                validateAssignment(line, localVariables);
            } else if (line.matches("(int|double|boolean|char|String).*")) {
                validateVariableDeclaration(line, localVariables);
            } else {
                validateMethodCall(line);
            }
        } else {
            throw new RuntimeException("Invalid line: " + line);
        }
    }

    private void validateAssignment(String line, Map<String, Variable<?>> localVariables) {
        String[] parts = line.split("\\s*=\\s*");
        String variableName = parts[0].trim();
        String value = parts[1].replace(";", "").trim();

        Variable<?> variable = localVariables.getOrDefault(variableName, globalMap.get(variableName));
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

    private void validateReturnStatement(String line) {
        if (!line.equals("return;")) {
            throw new RuntimeException("Invalid return statement: " + line);
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
        String methodCallPattern = "[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(([^)]*)\\)\\s*;";
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


    public void getAllFunctionsNames() throws RuntimeException{
//          if the line is a start of function decleration
//              check if it is a valid function decleration
//                  insert <function name, [types list])
        for(int i=0; i<linesNumber; i++){
            String line = linesArray.get(i);
            if (RowValidnessClass.isStartFunction(line)){
                checkFunctionDecleration(line);
            }
        }
    }

    private void checkFunctionDecleration(String line) throws RuntimeException {
        String[] splitted = line.split("\\(", 2); // Split into left and right parts at the first '('
        if (splitted.length != 2) {
            throw new RuntimeException("Invalid function declaration: Missing parentheses.");
        }
        String functionLeftPart = splitted[0];
        String functionRightPart = splitted[1].trim();

        // Validate the left part of the function declaration
        Pattern leftPattern = Pattern.compile("^\\s*void\\s+([a-zA-Z]\\w*)\\s*$");
        Matcher matcher = leftPattern.matcher(functionLeftPart);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid function declaration syntax: " + line);
        }

        // Extract function name
        String functionName = matcher.group(1);

        // Check for duplicate function names
        if (functionsMap.containsKey(functionName)) {
            throw new RuntimeException("Duplicate function name: " + functionName);
        }
        Pattern paramPattern = Pattern.compile("^\\s*((\\s*(int|double|boolean|char|String)" +
                "\\s+[a-zA-Z]\\w*\\s*)(,\\s*(int|double|boolean|char|String)\\s+[a-zA-Z]\\w*\\s*)*)?\\)\\s*\\{\\s*$");
        Matcher paramMatcher = paramPattern.matcher(functionRightPart);
        if (!paramMatcher.matches()) {
            throw new RuntimeException("Invalid function parameter syntax: " + line);
        }

        // Extract parameter types and names
        String paramList = paramMatcher.group(1);
//        List<Variable<?>> paramTypes = new ArrayList<>();

        List<Map<String, Variable<Object>>> parameterList = new ArrayList<>();

        if (paramList != null && !paramList.isEmpty()) { // Check for null and non-empty parameter list
            String[] params = paramList.split("\\s*,\\s*");
//            functionsMap.put(functionName, parameterList);
            for (String param : params) {
                String[] paramParts = param.trim().split("\\s+");
                if (paramParts.length != 2) {
                    throw new RuntimeException("Invalid parameter declaration: " + param);
                }

                String paramType = paramParts[0];
                String paramName = paramParts[1];
                if (!paramType.matches(VALID_TYPES)) {
                    throw new RuntimeException("Invalid parameter type : " + paramType);
                }

                Variable<Object> variable = new Variable<>(null, paramType, false);
                Map<String, Variable<Object>> varMap = new HashMap<>();
                varMap.put(paramName, variable);
                parameterList.add(varMap);
            }
            functionsMap.put(functionName, parameterList);
        }
        else{
            functionsMap.put(functionName, parameterList);
        }
    }

    public int processAllMethods() {
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
        }
        return 0; // All methods are valid
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
}