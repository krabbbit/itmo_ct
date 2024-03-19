package expression.generic;

import java.util.List;
import java.util.Objects;
import expression.generic.*;

public class Variable<T> extends abstractGeneralElems<T>{
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
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z, boolean ifChecked) {
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
    public MyType<T> evaluate(List<MyType<T>> variables, boolean ifChecked) {
        return variables.get(number);
    }

    @Override
    public MyType<T> evaluate(MyType<T> x) {
        return null;
    }
}
