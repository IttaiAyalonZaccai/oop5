package ex5.main;

import ex5.main.file_manager.FileProcessor;

import java.io.*;

public class Sjavac {
    public static final int SYNTAX_ERROR_EXIT_CODE = 1;
    public static final int IO_ERROR_EXIT_CODE = 2;

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
