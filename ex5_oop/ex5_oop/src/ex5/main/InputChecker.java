package ex5.main;

import java.io.IOException;

import static ex5.main.Sjavac.IO_ERROR_EXIT_CODE;

public class InputChecker {
    private static final String PARAMETERS_NUMBER_ERROR = "Invalid number of parameters.";
    private static final String INVALID_SUFFIX_ERROR = "File type is not .sjava.";
    private static final String SJAVA_VALID_SUFFIX = ".sjava";

    public static void checkInputParameter(String[] args) {
        try {
            if (args.length != 1) {
                throw new IOException(PARAMETERS_NUMBER_ERROR);
            }
            String fileName = args[0];
            if (!fileName.endsWith(SJAVA_VALID_SUFFIX)){
                throw new IOException(INVALID_SUFFIX_ERROR);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(IO_ERROR_EXIT_CODE);
        }
    }
}
