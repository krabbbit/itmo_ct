package expression;

import java.util.Objects;

public abstract class BinaryOperation implements Elements, TripleExpression{
    protected char simbol;
    protected Elements elem1;
    protected Elements elem2;
    public BinaryOperation (Elements a, Elements b) {
        elem1 = a;
        elem2 = b;
    }
    @Override
    public String toString() {
        return "(" + elem1.toString() +
                " " + simbol + " " +
                elem2.toString() +
                ")";
    }

    @Override
    public int evaluate(int x) {
        return switch (simbol) {
            case '*' -> elem1.evaluate(x) * elem2.evaluate(x);
            case '/' -> elem1.evaluate(x) / elem2.evaluate(x);
            case '-' -> elem1.evaluate(x) - elem2.evaluate(x);
            case '+' -> elem1.evaluate(x) + elem2.evaluate(x);
            default -> 0;
        };
    }
    @Override
    public int evaluate(int x, int y, int z) {
        return switch (simbol) {
            case '*' -> elem1.evaluate(x, y, z) * elem2.evaluate(x, y, z);
            case '/' -> elem1.evaluate(x, y, z) / elem2.evaluate(x, y, z);
            case '-' -> elem1.evaluate(x, y, z) - elem2.evaluate(x, y, z);
            case '+' -> elem1.evaluate(x, y, z) + elem2.evaluate(x, y, z);
            default -> 0;
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() != obj.getClass()) {
            return false;
        }
        return obj != null && this.elem1.equals(((BinaryOperation) obj).elem1) && this.elem2.equals(((BinaryOperation) obj).elem2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toString());
    }
}
