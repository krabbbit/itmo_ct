package expression.exceptions;

public class OverflowError extends NumberFormatException{
    public OverflowError (String s) {
        super(s);
    }
}
