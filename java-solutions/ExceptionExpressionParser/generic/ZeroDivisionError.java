package expression.generic;

public class ZeroDivisionError extends ArithmeticException{
    public ZeroDivisionError () {
        super("Divide by 0");
    }
}

