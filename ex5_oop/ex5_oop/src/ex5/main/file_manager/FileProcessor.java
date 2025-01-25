package ex5.main.file_manager;

import ex5.main.Variable;
import ex5.main.file_manager.functions.FunctionBodyValidator;
import ex5.main.file_manager.functions.FunctionNames;
import ex5.main.file_manager.global_variables.GlobalVariables;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static ex5.main.Sjavac.SYNTAX_ERROR_EXIT_CODE;
/**
 * Processes the lines of a file, checks for line validity,
 * and handles comments and blank lines.
 */
public class FileProcessor{
    //    constants
    private final List<String> linesArray;
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
        preprocessCheckLineValidity();
    }

    /**
     * Checks the validity of all lines in the file.
     *
     */
    public void preprocessCheckLineValidity() {
        String line;
        for (int lineIndex = 0; lineIndex < linesNumber; lineIndex++) {
            line = linesArray.get(lineIndex);
            try {
                RowValidnessClass.checkSuffixes(line, lineIndex);
                RowValidnessClass.checkMiddleComments(line, lineIndex);
                RowValidnessClass.checkLineFormat(line, lineIndex);
            } catch (RuntimeException e) {
                System.out.println(SYNTAX_ERROR_EXIT_CODE);
                System.out.println(e.getMessage());
                System.exit(SYNTAX_ERROR_EXIT_CODE);
            }
        }
    }
    /**
     * Checks the validity of global variables.
     *
     */
    public void checkGlobalVariables() {
        GlobalVariables globalVariables = new GlobalVariables(linesArray);
        try {
        globalVariables.validAndCreateGlobalMap();
        }
        catch (RuntimeException e) {
            System.out.println(SYNTAX_ERROR_EXIT_CODE);
            System.out.println(e.getMessage());
            System.exit(SYNTAX_ERROR_EXIT_CODE);
        }
        this.globalMap = globalVariables.getGlobalMap();
    }
    /**
     * Checks the validity of functions names.
     *
     */
    public void checkFunctionNames() {
        FunctionNames functionNames = new FunctionNames(linesArray);
        try {
            functionNames.getAllFunctionsNames();
        }
        catch (RuntimeException e) {
            System.out.println(SYNTAX_ERROR_EXIT_CODE);
            System.out.println(e.getMessage());
            System.exit(SYNTAX_ERROR_EXIT_CODE);
        }
        this.functionsMap = functionNames.getFunctionsMap();
    }
    /**
     * Checks the validity functio body.
     *
     */
    public void checkFunctionsBody() {
        FunctionBodyValidator functionBodyValidator = new FunctionBodyValidator(linesArray,
                                                                                globalMap,
                                                                                functionsMap);
        functionBodyValidator.processAllMethods();
    }
}