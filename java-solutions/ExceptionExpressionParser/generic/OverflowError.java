package expression.generic;

public class OverflowError extends NumberFormatException{
    public OverflowError (String s) {
        super(s);
    }
}
