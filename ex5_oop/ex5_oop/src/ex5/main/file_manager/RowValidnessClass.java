package ex5.main.file_manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RowValidnessClass {
    public static void check_suffixes(String line, int lineCounter) throws RuntimeException {
        Pattern pattern = Pattern.compile("//.*|.*[;{}]\\s*$");
        Matcher mac = pattern.matcher(line);
        System.out.println(mac.matches());
        if (mac.matches()){
//            System.out.println("debug massage: valid line");

        }
        else {
            System.out.println();
            throw new RuntimeException("ERROR in line " + lineCounter + " not supported comment value!");
        }
    }

    public static void checkMiddleComments(String line, int lineCounter) throws RuntimeException {
        Pattern pattern = Pattern.compile("/\\*|\\*/|.//");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            throw new RuntimeException("ERROR in line " + lineCounter + " not supported comment value " +
                    "in the middle of the line!");
        }
    }

}
