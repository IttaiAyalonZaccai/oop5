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
            fileProcessor = new FileProcessor(bufferedReader);
            fileProcessor.checkLineValidity();


//            fileProcessor.printIgnoredLines();

            // todo:
            //  3) loop over lines in file:
            //      a) check Validness of each line
            //      b) remove spaces, etc
//                try (lineValidation(line)){}
//                catch (RuntimeException e){
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(IO_ERROR_EXIT_CODE);
        }



        // todo: Step 2: Validation Check for Entire File.
    }


// todo: make sure there are no arrays and operators 5.1
//                }

}


// List of Regex expressions:
// A code line, which must end with one of the following suffixes:
//    .*[;{}]\s*$

//    Pattern pat = Pattern.compile("(050|052|054|057|059)[0-9]{7}");
