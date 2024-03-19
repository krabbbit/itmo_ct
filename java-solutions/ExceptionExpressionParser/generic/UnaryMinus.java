package expression.generic;

//import expression.exceptions.OverflowError;
import expression.common.Type;
import expression.generic.*;
import java.util.List;
import java.util.Objects;

public class UnaryMinus<T> extends abstractGeneralElems<T> {
    protected abstractGeneralElems<T> elem1;
    protected MyType<T> minus;
    public UnaryMinus (abstractGeneralElems<T> a, MyType<T> minus) {
        elem1 = a;
        this.minus = minus;
    }
    @Override
    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z, boolean isCheck) {
        MyType<T> a = elem1.evaluate(x, y, z, isCheck);
//        if (a == Integer.MIN_VALUE) {
//            throw new OverflowError();
//        }
        return a.calc("*", minus.getValue(), isCheck);
    }

    @Override
    public MyType<T> evaluate(List<MyType<T>> variables, boolean isCheck) {
        MyType<T> a = elem1.evaluate(variables, isCheck);
        return a.calc("*", minus.getValue(), isCheck);
//        if (a == Integer.MIN_VALUE) {
//            throw new OverflowError();
//        }
    }

    @Override
    public MyType<T> evaluate(MyType<T> x) {
        return null;
    }
}
