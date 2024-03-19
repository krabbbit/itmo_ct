package expression;

public class CheckedDivide extends CheckedBinaryOperation implements Elements, TripleExpression{
    public CheckedDivide(Elements a, Elements b) {
        super(a, b);
        if (b.toString().equals("0")) {
            throw new ArithmeticException("divide by zero");
        }
        this.simbol = '/';
    }
}

