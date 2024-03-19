package expression.exceptions;

import expression.BinaryOperation;
import expression.Elements;
import expression.ListExpression;

import java.util.List;

public class CheckedBinaryOperation extends BinaryOperation implements ListExpression {

    public CheckedBinaryOperation(Elements a, Elements b) {
        super(a, b);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int a = elem1.evaluate(x, y, z);
        int b = elem2.evaluate(x, y, z);
        if (simbol == '*' && !(a > 0 && b > 0 && ((Integer.MAX_VALUE / a) < b) ||
                        (a < 0 && b < 0 && ((Integer.MAX_VALUE / a) > b)) ||
                (a < 0 && b > 0 && (Integer.MIN_VALUE / b > a)) ||
                (a > 0 && b < 0 && (Integer.MIN_VALUE / a > b)))) {
            return a * b;
        } else if (simbol == '/') {
            if (b == 0) {
                throw new ZeroDivisionError();
            } else if (!(a == Integer.MIN_VALUE && b == -1)){
                return a / b;
            } else {
                throw new OverflowError("Calculation overflow");
            }
        } else if (simbol == '+' && !((a > 0 && b > 0 && ((a + b) < a || (a + b) < b)) || (a < 0 && b < 0 && ((a + b) > a || (a + b) > b)))) {
            return a + b;
        } else if (simbol == '-' && !(b > 0 && ((Integer.MIN_VALUE + b) > a) ||
                (b < 0 && ((Integer.MAX_VALUE + b) < a)))) {
            return a - b;
        } else {
            throw new OverflowError("Calculation overflow");
        }
    }
    @Override
    public int evaluate(List<Integer> variables) {
        int a = elem1.evaluate(variables);
        int b = elem2.evaluate(variables);
        if (simbol == '*' && !(a > 0 && b > 0 && ((Integer.MAX_VALUE / a) < b) ||
                (a < 0 && b < 0 && ((Integer.MAX_VALUE / a) > b)) ||
                (a < 0 && b > 0 && (Integer.MIN_VALUE / b > a)) ||
                (a > 0 && b < 0 && (Integer.MIN_VALUE / a > b)))) {
            return a * b;
        } else if (simbol == '/') {
            if (b == 0) {
                throw new ZeroDivisionError();
            } else if (!(a == Integer.MIN_VALUE && b == -1)){
                return a / b;
            } else {
                throw new OverflowError("Calculation overflow");
            }
        } else if (simbol == '+' && !((a > 0 && b > 0 && ((a + b) < a || (a + b) < b)) || (a < 0 && b < 0 && ((a + b) > a || (a + b) > b)))) {
            return a + b;
        } else if (simbol == '-' && !(b > 0 && ((Integer.MIN_VALUE + b) > a) ||
                (b < 0 && ((Integer.MAX_VALUE + b) < a)))) {
            return a - b;
        } else {
            throw new OverflowError("Calculation overflow");
        }
    }
}
