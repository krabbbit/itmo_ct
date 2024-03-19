package expression.exceptions;

import expression.Elements;
import expression.TripleExpression;

public class CheckedMultiply extends CheckedBinaryOperation implements Elements, TripleExpression {
    public CheckedMultiply(Elements a, Elements b) {
        super(a, b);
        this.simbol = '*';
    }

}

