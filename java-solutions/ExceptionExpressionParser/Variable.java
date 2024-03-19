package expression;

import java.util.List;
import java.util.Objects;

public class Variable implements Elements, ListExpression{
    private String name;
    private int number;

    public Variable (String s) {
        name = s;
    }
    public Variable (int v) {
        number = v;
    }
    public void setName (String s) {
        name = s;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }
    @Override
    public int evaluate(int x, int y, int z) {
        switch (name) {
            case "x" -> {
                return x;
            }
            case "y" -> {
                return y;
            }
            case "z" -> {
                return z;
            }
        }
        return x;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object e) {
        return (e instanceof Variable) && (((Variable) e).name.equals(this.name));
    }
    public int hashCode() {
        return Objects.hash(this.toString());
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return variables.get(number);
    }
}
