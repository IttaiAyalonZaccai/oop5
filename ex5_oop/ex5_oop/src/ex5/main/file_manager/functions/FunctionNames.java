package ex5.main.file_manager.functions;

import ex5.main.Variable;
import ex5.main.file_manager.RowValidnessClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionNames {
    private int linesNumber;
    private HashMap<String, List<Map<String, Variable<Object>>>> functionsMap = new HashMap<>();
    private final List<String> linesArray;
    private final String VALID_TYPES = "int|double|boolean|char|String";


    public FunctionNames(List<String> linesArray) {
        this.linesArray = linesArray;
        this.linesNumber = linesArray.size();
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

    public HashMap<String, List<Map<String, Variable<Object>>>> getFunctionsMap(){
        return functionsMap;
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
}
