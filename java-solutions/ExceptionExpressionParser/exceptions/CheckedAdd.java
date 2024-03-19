package expression.exceptions;

import expression.Elements;
import expression.TripleExpression;

public class CheckedAdd extends CheckedBinaryOperation implements Elements, TripleExpression {
    public CheckedAdd(Elements a, Elements b) {
        super(a, b);
        this.simbol = '+';
    }


}

