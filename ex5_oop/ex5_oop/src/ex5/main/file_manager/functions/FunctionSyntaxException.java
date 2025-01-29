package ex5.main.file_manager.functions;

/**
 * Exception thrown when a function's syntax is invalid.
 * This exception is a subclass of {@link RuntimeException} and is used to
 * indicate errors related to incorrect function syntax.
 */
public class FunctionSyntaxException extends RuntimeException {

    /**
     * Constructs a new FunctionSyntaxException with the specified error message.
     *
     * @param message the detail message explaining the syntax error
     */
    public FunctionSyntaxException(String message) {
        super(message);
    }
}
