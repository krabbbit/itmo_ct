package expression;

import java.util.List;
import java.util.Objects;

public class Const implements Elements{
    public int constanta;

    public Const (int c) {
        this.constanta = c;
    }

    @Override
    public int evaluate(int x) {
        return constanta;
    }
    @Override
    public int evaluate(int x, int y, int z) {
        return constanta;
    }

    @Override
    public String toString() {
        return Integer.toString(constanta);
    }

    @Override
    public boolean equals(Object e) {
        return (e instanceof Const) && (((Const) e).constanta == this.constanta);
    }
    public int hashCode() {
        return Objects.hash(this.toString());
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return constanta;
    }
}
