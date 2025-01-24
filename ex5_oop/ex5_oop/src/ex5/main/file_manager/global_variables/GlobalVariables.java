package ex5.main.file_manager.global_variables;

import ex5.main.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The GlobalVariables class processes and validates global variable declarations in s-Java code.
 * It ensures the correct syntax, type compatibility, and uniqueness of global variables.
 */
public class GlobalVariables {
    private static final String ERROR_UNMATCHED_CLOSING_BRACE = "Unmatched closing brace encountered.";
    private static final String ERROR_UNMATCHED_OPENING_BRACE = "Unmatched opening brace(s) detected.";
    private static final String ERROR_INVALID_DECLARATION = "Invalid global variable declaration: ";
    private static final String ERROR_DUPLICATE_VARIABLE = "Duplicate global variable name: ";
    private static final String ERROR_TYPE_MISMATCH = "Type mismatch: Cannot assign ";
    private static final String ERROR_UNKNOWN_TYPE = "Unknown type: ";
    private static final String ERROR_INVALID_INT = "Invalid value for type int: ";
    private static final String ERROR_INVALID_DOUBLE = "Invalid value for type double: ";
    private static final String ERROR_INVALID_BOOLEAN = "Invalid value for type boolean: ";
    private static final String ERROR_INVALID_CHAR = "Invalid value for type char: ";
    private static final String ERROR_INVALID_STRING = "Invalid value for type String: ";

    private static final String VALID_TYPES = "int|double|boolean|char|String";
    private static final String VARIABLE_NAME_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";
    private static final String VALUE_PATTERN = ".*";
    private static final String INT_PATTERN = "-?\\d+";
    private static final String DOUBLE_PATTERN = "-?\\d*\\.\\d+|-?\\d+\\.\\d*|-?\\d+";
    private static final String BOOLEAN_PATTERN = "true|false";
    private static final String CHAR_PATTERN = "'.'";
    private static final String STRING_PATTERN = "\"[^\"]*\"";

    private static final String DECLARATION_PATTERN = String.format(
            "^(final\\s+)?(%s)\\s+(%s(\\s*=\\s*%s)?(\\s*,\\s*%s(\\s*=\\s*%s)?)*)\\s*;$",
            VALID_TYPES, VARIABLE_NAME_PATTERN, VALUE_PATTERN, VARIABLE_NAME_PATTERN, VALUE_PATTERN
    );
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final String STRING = "String";
    private static final int INT1 = 1;
    private static final String TO = " to ";
    private static final String ASSIGNING_TO_VALUE_NULL = "assigning to value 'null'";

    private final List<String> linesArray;
    private final HashMap<String, Variable<?>> globalMap = new HashMap<>();

    /**
     * Constructor for GlobalVariables.
     *
     * @param linesArray List of code lines.
     */
    public GlobalVariables(List<String> linesArray) {
        this.linesArray = linesArray;
    }

    /**
     * Retrieves the global variable map.
     *
     * @return HashMap containing global variable names and values.
     */
    public HashMap<String, Variable<?>> getGlobalMap() {
        return globalMap;
    }

    /**
     * Validates and creates the global variable map from the given lines of code.
     *
     * @throws RuntimeException if there are unmatched braces or invalid declarations.
     */
    public void validAndCreateGlobalMap() throws RuntimeException {
        int scopeLevel = 0;
        for (String line : linesArray) {
            line = line.trim();
            if (line.endsWith("{")) {
                scopeLevel++;
                continue;
            }
            if (line.endsWith("}")) {
                if (scopeLevel > 0) {
                    scopeLevel--;
                } else {
                    throw new RuntimeException(ERROR_UNMATCHED_CLOSING_BRACE);
                }
                continue;
            }
            if (scopeLevel == 0) {
                validateAndAddGlobalVariable(line);
            }
        }
        if (scopeLevel != 0) {
            throw new RuntimeException(ERROR_UNMATCHED_OPENING_BRACE);
        }
    }

    private void validateAndAddGlobalVariable(String line) throws RuntimeException {
        line = line.trim();
        Pattern pattern = Pattern.compile(DECLARATION_PATTERN);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            throw new RuntimeException(ERROR_INVALID_DECLARATION + line);
        }

        boolean isFinal = matcher.group(INT1) != null;
        String type = matcher.group(2);
        String variables = matcher.group(3);

        String[] variableParts = variables.split("\\s*,\\s*");
        for (String varPart : variableParts) {
            String[] nameValue = varPart.split("\\s*=\\s*");
            String name = nameValue[0];
            String value = nameValue.length > INT1 ? nameValue[INT1] : null;

            if (globalMap.containsKey(name)) {
                throw new RuntimeException(ERROR_DUPLICATE_VARIABLE + name);
            }

            Object resolvedValue = null;
            if (value != null) {
                resolvedValue = validateValue(type, value);
            }

            Variable<Object> variable = new Variable<>(resolvedValue, type, isFinal);
            globalMap.put(name, variable);
        }
    }

    private Object validateValue(String type, String value) throws RuntimeException {
        if (globalMap.containsKey(value)) {
            Variable<?> sourceVar = globalMap.get(value);
            if (sourceVar.getValue() == null) {
                throw new RuntimeException(ASSIGNING_TO_VALUE_NULL);
            }
            if (!type.equals(sourceVar.getType())) {
                throw new RuntimeException(ERROR_TYPE_MISMATCH + sourceVar.getType() + TO + type);
            }
            return sourceVar.getValue();
        }

        switch (type) {
            case INT:
                if (value.matches(INT_PATTERN)) {
                    return Integer.parseInt(value);
                }
                throw new RuntimeException(ERROR_INVALID_INT + value);

            case DOUBLE:
                if (value.matches(DOUBLE_PATTERN)) {
                    return Double.parseDouble(value);
                }
                throw new RuntimeException(ERROR_INVALID_DOUBLE + value);

            case BOOLEAN:
                if (value.matches(BOOLEAN_PATTERN)) {
                    return Boolean.parseBoolean(value);
                }
                if (value.matches(DOUBLE_PATTERN)) {
                    return Double.parseDouble(value) != 0;
                }
                throw new RuntimeException(ERROR_INVALID_BOOLEAN + value);

            case CHAR:
                if (value.matches(CHAR_PATTERN)) {
                    return value.charAt(INT1);
                }
                throw new RuntimeException(ERROR_INVALID_CHAR + value);

            case STRING:
                if (value.matches(STRING_PATTERN)) {
                    return value.substring(INT1, value.length() - INT1);
                }
                throw new RuntimeException(ERROR_INVALID_STRING + value);

            default:
                throw new RuntimeException(ERROR_UNKNOWN_TYPE + type);
        }
    }
}
