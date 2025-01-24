package ex5.main;

import ex5.main.file_manager.FileProcessor;

import java.io.*;


/**
 * Class: Sjavac
 * The main entry point for the s-Java compiler. Validates and processes an s-Java source file.
 * Handles input checking, file parsing, global variable validation, function name validation,
 * and function body validation.
 */
public class Sjavac {
    public static final int SYNTAX_ERROR_EXIT_CODE = 1;
    public static final int IO_ERROR_EXIT_CODE = 2;

    /**
     * Main method to process and validate an s-Java file.
     * The program takes a single command-line argument, which is the path to the s-Java file.
     * It performs the following steps:
     * 1. Validates the input arguments using {@link InputChecker}.
     * 2. Reads the file specified by the input path.
     * 3. Processes the file using {@link FileProcessor}.
     *    - Checks global variable declarations.
     *    - Validates function names.
     *    - Validates the body of each function.
     * 4. Exits with the appropriate error code if syntax or I/O errors occur.
     *
     * @param args Command-line arguments. Should contain exactly one argument: the path to the s-Java file.
     */
    public static void main(String[] args) {
        InputChecker.checkInputParameter(args);
        String path = args[0];
        try (
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            // PreProcess:
            FileProcessor fileProcessor = new FileProcessor(bufferedReader);
            fileProcessor.checkGlobalVariables();
            fileProcessor.checkFunctionNames();
            // validate file
            fileProcessor.checkFunctionsBody();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(IO_ERROR_EXIT_CODE);
        }
    }
}
