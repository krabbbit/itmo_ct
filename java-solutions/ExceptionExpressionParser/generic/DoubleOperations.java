package expression.generic;

import java.math.BigInteger;

public class DoubleOperations extends abstractTypes<Double>{
    public DoubleOperations(Double cur) {
        super(cur);
    }

    @Override
    protected MyType<Double> getTypeCurrent(Double current) {
        return new DoubleOperations(current);
    }

    public static MyType<Double> toType(int exp) {
        return new DoubleOperations((double)exp);
    }

    @Override
    protected Double check(String simbol, Double b, boolean isCheck) {
        return (double) 0;
    }

    @Override
    protected Double doThisImpl(Double a, String operator) {
        return switch (operator) {
            case("+") -> current + a;
            case("-") -> current - a;
            case("*") -> current * a;
            case("/") -> current / a;
            default -> (double)0;
        };
    }


//    protected MyType<Integer> doOperationImpl(Integer a, Integer b, String operator) {
//        return switch (operator) {
//            case("+") -> a + b;
//            case("-") -> a - b;
//            case("*") -> a * b;
//            case("/") -> a / b;
//            default -> 0;
//        };
//    }

    @Override
    public Double getValue() {
        return current;
    }
}
