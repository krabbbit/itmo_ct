package expression.exceptions;

import expression.Elements;
import expression.TripleExpression;

public class CheckedSubtract extends CheckedBinaryOperation implements Elements, TripleExpression {
    public CheckedSubtract(Elements a, Elements b) {
        super(a, b);
        this.simbol = '-';
    }

}

