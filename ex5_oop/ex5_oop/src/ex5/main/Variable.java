/**
 * This class represents a generic variable that holds a value, its type, and a finality status.
 * It is designed to store metadata for variables in a simplified Java environment.
 * @param <T> The type of the value stored in the variable.
 */
package ex5.main;

public class Variable<T> {
    public static final String TO_STR_FORMAT_VARIABLE = "Variable{";
    public static final String TO_STR_FORMAT_VALUE = "value=";
    public static final String TO_STR_FORMAT_TYPE = ", type='";
    public static final String TO_STR_FORMAT_FINAL = ", isFinal=";
    public static final char CLOSE_BRACE = '}';
    private final T value;
    private final String type;
    private final boolean isFinal;

    /**
     * Constructs a new Variable instance.
     * @param value   The value of the variable.
     * @param type    The type of the variable. It should match the actual type of the value.
     *                Accepted types: {@code TYPE_INTEGER}, {@code TYPE_STRING}, {@code TYPE_BOOLEAN}.
     * @param isFinal Indicates whether the variable is final (immutable).
     */
    public Variable(T value, String type, boolean isFinal) {
        this.value = value;
        this.type = type;
        this.isFinal = isFinal;
    }

    /**
     * Retrieves the value of the variable.
     * @return The value stored in the variable.
     */
    public T getValue() {
        return value;
    }

    /**
     * Retrieves the type of the variable.
     * @return A string representing the type of the variable.
     */
    public String getType() {
        return type;
    }

    /**
     * Checks if the variable is final (immutable).
     * @return {@code true} if the variable is final, {@code false} otherwise.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Returns a string representation of the variable.
     * @return A string describing the variable, including its value, type, and finality.
     */
    @Override
    public String toString() {
        return TO_STR_FORMAT_VARIABLE +
                TO_STR_FORMAT_VALUE + value +
                TO_STR_FORMAT_TYPE + type + '\'' +
                TO_STR_FORMAT_FINAL + isFinal +
                CLOSE_BRACE;
    }
}
