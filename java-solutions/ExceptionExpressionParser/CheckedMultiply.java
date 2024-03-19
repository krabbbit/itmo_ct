package expression;

public class CheckedMultiply extends CheckedBinaryOperation implements Elements, TripleExpression{
    public CheckedMultiply(Elements a, Elements b) {
        super(a, b);
        this.simbol = '*';
    }

}

