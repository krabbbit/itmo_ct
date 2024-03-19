package expression;

public class CheckedAdd extends CheckedBinaryOperation implements Elements, TripleExpression{
    public CheckedAdd(Elements a, Elements b) {
        super(a, b);
        this.simbol = '+';
    }
}

