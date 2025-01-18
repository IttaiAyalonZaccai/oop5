package ex5.main.file_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ex5.main.Sjavac.SYNTAX_ERROR_EXIT_CODE;
/**
 * Processes the lines of a file, checks for line validity,
 * and handles comments and blank lines.
 */
public class FileProcessor{
    private final List<String> linesArray;
    private List<Integer> ignoredLinesIndexes = new ArrayList<>();
    private int linesNumber = 0;
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
    }
    /**
     * Retrieves the line at the specified index.
     *
     * @param index The index of the line to retrieve.
     * @return The line at the specified index.
     */
    public String getLine(int index) {
        return linesArray.get(index);
    }

    /**
     * Checks the validity of all lines in the file.
     *
     * @throws IOException If an I/O error occurs during the process.
     */
    public void checkLineValidity() throws IOException {
        String line;
        for (int lineIndex = 0; lineIndex < linesNumber; lineIndex++) {
            line = linesArray.get(lineIndex);
            try {
                RowValidnessClass.check_suffixes(line, lineIndex);
                RowValidnessClass.checkMiddleComments(line, lineIndex);
                checkIfLineIsCommentOrBlank(line, lineIndex);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                System.exit(SYNTAX_ERROR_EXIT_CODE);
            }
        }

    }



    /**
     * Checks if a line is a comment or blank line and adds its index to ignored lines.
     *
     * @param line  The line to check.
     * @param index The index of the line being checked.
     */
    private void checkIfLineIsCommentOrBlank(String line, int index) {
        Pattern pattern = Pattern.compile("//.*|\\s*");
        Matcher mac = pattern.matcher(line);
        if (mac.matches()) {
            ignoredLinesIndexes.add(index);
        }
    }

    public void printIgnoredLines() {
        for(int i: ignoredLinesIndexes){
            System.out.println(linesArray.get(i));
        }
    }
}
