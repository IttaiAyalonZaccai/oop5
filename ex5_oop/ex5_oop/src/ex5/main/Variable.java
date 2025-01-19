package ex5.main;

public class Variable <T>{
    private final T value;
    private final String type;
    private final boolean isFinal;

    public Variable(T value, String type, boolean isFinal) {
        this.value = value;
        this.type = type;
        this.isFinal = isFinal;
    }

    public T getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "value=" + value +
                ", type='" + type + '\'' +
                ", isFinal=" + isFinal +
                '}';
    }
}
