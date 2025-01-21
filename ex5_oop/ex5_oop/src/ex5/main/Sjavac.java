package ex5.main;

import ex5.main.file_manager.FileProcessor;
import ex5.main.file_manager.RowValidnessClass;

import java.io.*;

public class Sjavac {

    public static final int SYNTAX_ERROR_EXIT_CODE = 1;
    public static final int IO_ERROR_EXIT_CODE = 2;

    public static void main(String[] args) {
        // 1) todo Validation of argument:

        // 2) open file
        String path = args[0];
        FileProcessor fileProcessor;
        try (
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            // PreProcess:
            fileProcessor = new FileProcessor(bufferedReader);
            fileProcessor.validAndCreateGlobalMap();
            fileProcessor.getAllFunctionsNames();
            // validate file
            fileProcessor.processAllMethods();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(IO_ERROR_EXIT_CODE);
        }
    }
// todo: make sure there are no arrays and operators 5.1
}
