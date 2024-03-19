package expression;

import expression.exceptions.OverflowError;

import java.util.List;
import java.util.Objects;

public class UnaryMinus implements Elements, TripleExpression, ListExpression{
    protected Elements elem1;
    public UnaryMinus (Elements a) {
        elem1 = a;
    }
    @Override
    public String toString() {
        return "-(" + elem1.toString() + ")";
    }

    @Override
    public int evaluate(int x) {
        return -1 * elem1.evaluate(x);
    }
    @Override
    public int evaluate(int x, int y, int z) {
//        int a = elem1.evaluate(x, y, z);
//        if (a == Integer.MIN_VALUE) {
//            throw new OverflowError();
//        }
        return -1 * elem1.evaluate(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() != obj.getClass()) {
            return false;
        }
        return obj != null && this.elem1.equals(((UnaryMinus) obj).elem1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toString());
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return 0;
    }
}
