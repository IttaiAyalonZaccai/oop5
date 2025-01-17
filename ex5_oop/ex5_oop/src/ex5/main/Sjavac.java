package ex5.main;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sjavac {
    public static void main(String[] args) {
        // 1) todo Validation of argument:

        // 2) open file
        String path = args[0];
        String line;
        try (
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
            ) {
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                // todo:
                //  3) loop over lines in file:
                //      a) check Validness of each line
                //      b) remove spaces, etc
                try (lineValidation(line)){}
                catch (lineException e){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // todo: Step 2: Validation Check for Entire File.
    }
}


// List of Regex expressions:
// A code line, which must end with one of the following suffixes:
//    .*[;{}]\s*$