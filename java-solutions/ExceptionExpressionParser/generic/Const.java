package expression.generic;

import java.util.List;
import java.util.Objects;
import expression.generic.*;

public class Const<T> extends abstractGeneralElems<T> {
    public MyType<T> constanta;

    public Const (MyType<T> c) {
        this.constanta = c;
    }

    @Override
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z, boolean ifChecked) {
        return constanta;
    }

    @Override
    public MyType<T> evaluate(List<MyType<T>> variables, boolean ifChecked) {
        return constanta;
    }

    @Override
    public MyType<T> evaluate(MyType<T> x) {
        return null;
    }
}
