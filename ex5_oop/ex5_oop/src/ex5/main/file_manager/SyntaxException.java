package ex5.main.file_manager;

/**
 * Exception thrown when a syntax error occurs in file management operations.
 * This exception extends {@link RuntimeException} and is used to indicate
 * syntax-related errors in the application.
 *
 * @author [Your Name]
 * @version 1.0
 */
public class SyntaxException extends RuntimeException {

    /**
     * Constructs a new SyntaxException with the specified error message.
     *
     * @param message the detail message explaining the syntax error
     */
    public SyntaxException(String message) {
        super(message);
    }
}
