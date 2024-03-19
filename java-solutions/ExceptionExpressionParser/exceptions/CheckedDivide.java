package expression.exceptions;

import expression.Elements;
import expression.TripleExpression;

public class CheckedDivide extends CheckedBinaryOperation implements Elements, TripleExpression {
    public CheckedDivide(Elements a, Elements b) {
        super(a, b);
        this.simbol = '/';
    }
}

