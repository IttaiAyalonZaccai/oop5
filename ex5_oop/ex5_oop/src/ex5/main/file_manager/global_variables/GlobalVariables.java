package ex5.main.file_manager.global_variables;

import ex5.main.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalVariables {
    private final List<String> linesArray;
    private HashMap<String, Variable<?>> globalMap = new HashMap<>();
    public GlobalVariables(List<String> linesArray) {
        this.linesArray = linesArray;
    }

    public HashMap<String, Variable<?>> getGlobalMap(){
        return globalMap;
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
//            if (!isTypeCompatible(type, sourceVar.getType())) {
            if (!type.equals(sourceVar.getType())) {
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
}
