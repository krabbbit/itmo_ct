package expression;

import expression.exceptions.OverflowError;

import java.util.List;

public class CheckedBinaryOperation extends BinaryOperation{

    public CheckedBinaryOperation(Elements a, Elements b) {
        super(a, b);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        try {
            return Integer.parseInt(Integer.toString(super.evaluate(x, y, z)));
        } catch (NumberFormatException e) {
            throw new OverflowError("lala");
        }
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return 0;
    }
}
