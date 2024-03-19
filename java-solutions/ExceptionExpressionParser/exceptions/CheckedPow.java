package expression.exceptions;

import expression.Const;
import expression.Elements;
import expression.TripleExpression;

import java.util.List;
import java.util.Objects;

public class CheckedPow implements TripleExpression, Elements {
    protected Elements elem1;
    public CheckedPow(Elements a) {
        elem1 = a;
    }

    @Override
    public String toString() {
        return "pow2(" + elem1.toString() + ")";
    }

    private void check (int a) {
        if (a < 0) {
            throw new CalculationException("Negative exponent");
        } else if (a >= 31) {
            throw new OverflowError("Pow overflow");
        }
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int a = elem1.evaluate(x, y, z);
        check(a);
        int pow = 1;
        for (int i = 0; i < a; i++) {
            pow = new CheckedMultiply(new Const(pow), new Const(2)).evaluate(0);
        }
        return pow;
    }

    @Override
    public int evaluate(int x) {
        int a = elem1.evaluate(x);
        check(a);
        int pow = 1;
        for (int i = 0; i < a; i++) {
            pow = new CheckedMultiply(new Const(pow), new Const(2)).evaluate(0);
        }
        return pow;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() != obj.getClass()) {
            return false;
        }
        return obj != null && this.elem1.equals(((CheckedPow) obj).elem1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toString());
    }

    @Override
    public int evaluate(List<Integer> variables) {
        int a = elem1.evaluate(variables);
        check(a);
        int pow = 1;
        for (int i = 0; i < a; i++) {
            pow = new CheckedMultiply(new Const(pow), new Const(2)).evaluate(variables);
        }
        return pow;
    }
}
