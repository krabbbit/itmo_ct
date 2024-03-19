package expression.generic;

//import expression.BinaryOperation;
//import expression.Elements;

import java.util.List;

public class CheckedBinaryOperation<T> extends abstractGeneralElems<T> implements TripleExpression<T>, ListExpression<T> {
    abstractGeneralElems<T> elem1, elem2;
    char simbol;

    public CheckedBinaryOperation(abstractGeneralElems<T> a, abstractGeneralElems<T> b) {
        elem1 = a;
        elem2 = b;
    }

//    private int check(int a, int b) {
//        if (simbol == '*' && !(a > 0 && b > 0 && ((Integer.MAX_VALUE / a) < b) ||
//                (a < 0 && b < 0 && ((Integer.MAX_VALUE / a) > b)) ||
//                (a < 0 && b > 0 && (Integer.MIN_VALUE / b > a)) ||
//                (a > 0 && b < 0 && (Integer.MIN_VALUE / a > b)))) {
//            return a * b;
//        } else if (simbol == '/') {
//            if (b == 0) {
//                throw new ZeroDivisionError();
//            } else if (!(a == T.MIN_VALUE && b == -1)){
//                return a / b;
//            } else {
//                throw new OverflowError("Calculation overflow");
//            }
//        } else if (simbol == '+' && !((a > 0 && b > 0 && ((a + b) < a || (a + b) < b)) || (a < 0 && b < 0 && ((a + b) > a || (a + b) > b)))) {
//            return a + b;
//        } else if (simbol == '-' && !(b > 0 && ((T.MIN_VALUE + b) > a) ||
//                (b < 0 && ((T.MAX_VALUE + b) < a)))) {
//            return a - b;
//        } else {
//            throw new OverflowError("Calculation overflow");
//        }
//    }

    public MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z, boolean ifChecked) {
        MyType<T> a = elem1.evaluate(x, y, z, ifChecked);
        MyType<T> b = elem2.evaluate(x, y, z, ifChecked);
       // abstractTypes<T> integ = new UncheckedIntegerOperations(a.getValue().intValue());
        return a.calc(String.valueOf(simbol), b.getValue(), ifChecked);
    }

    public MyType<T> evaluate(List<MyType<T>> variables, boolean ifChecked) {
        MyType<T> a = elem1.evaluate(variables, ifChecked);
        MyType<T> b = elem2.evaluate(variables, ifChecked);
        // abstractTypes<T> integ = new UncheckedIntegerOperations(a.getValue().intValue());
        return a.calc(String.valueOf(simbol), b.getValue(), ifChecked);
    }


    @Override
    public MyType<T> evaluate(MyType<T> x) {
        return null;
    }
}
