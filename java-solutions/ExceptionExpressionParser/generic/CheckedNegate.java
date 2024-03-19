//package expression.generic;
//
//import expression.Elements;
//import expression.TripleExpression;
//import expression.UnaryMinus;
//
//import java.util.List;
//
//public class CheckedNegate<T extends Number> extends UnaryMinus<T> {
//    public CheckedNegate(Elements<T> a) {
//        super(a);
//    }
//
//    private T check (T a) {
//        return check(a);
//    }
//
//    @Override
//    public T evaluate(T x, T y, T z) {
//        T a = elem1.evaluate(x, y, z);
//        return check(a);
//    }
//    public T evaluate(List<T> variables) {
//        T a = elem1.evaluate(variables);
//        return check(a);
//    }
//}
