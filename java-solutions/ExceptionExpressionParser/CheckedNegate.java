package expression;

import java.util.List;

public class CheckedNegate extends UnaryMinus implements TripleExpression, Elements, ListExpression{
    public CheckedNegate(Elements a) {
        super(a);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        try {
            return Integer.parseInt(Integer.toString(super.evaluate(x, y, z)));
        } catch (NumberFormatException e) {
            throw new ArithmeticException("overflow");
        }
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return 0;
    }
    //?????????????????
}
