package expression.exceptions;

import expression.Elements;
import expression.TripleExpression;
import expression.UnaryMinus;

import java.util.List;

public class CheckedNegate extends UnaryMinus implements TripleExpression, Elements {
    public CheckedNegate(Elements a) {
        super(a);
    }

    private int check (int a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowError("Unary minus overflow");
        }
        return -1 * a;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int a = elem1.evaluate(x, y, z);
        return check(a);
    }
    public int evaluate(List<Integer> variables) {
        int a = elem1.evaluate(variables);
        return check(a);
    }
}
