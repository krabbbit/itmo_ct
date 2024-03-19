package expression.generic;

import java.math.BigInteger;
import java.util.Objects;

public class UncheckedIntegerOperations extends abstractTypes<Integer>{

    public UncheckedIntegerOperations(Integer cur) {
        super(cur);
    }

    @Override
    protected MyType<Integer> getTypeCurrent(Integer current) {
        return new UncheckedIntegerOperations(current);
    }

    @Override
    protected Integer check(String simbol, Integer b, boolean isCheck) {
        if (Objects.equals(simbol, "*") && !(current > 0 && b > 0 && ((Integer.MAX_VALUE / current) < b) ||
                (current < 0 && b < 0 && ((Integer.MAX_VALUE / current) > b)) ||
                (current < 0 && b > 0 && (Integer.MIN_VALUE / b > current)) ||
                (current > 0 && b < 0 && (Integer.MIN_VALUE / current > b)))) {
            return current * b;
        } else if (Objects.equals(simbol, "/")) {
            if (b == 0) {
                throw new ZeroDivisionError();
            } else if (!(current == Integer.MIN_VALUE && b == -1)){
                return current / b;
            } else {
                throw new OverflowError("Calculation overflow");
            }
        } else if (Objects.equals(simbol, "+") && !((current > 0 && b > 0 && ((current + b) < current || (current + b) < b))
                || (current < 0 && b < 0 && ((current + b) > current || (current + b) > b)))) {
            return current + b;
        } else if (Objects.equals(simbol, "-") && !(b > 0 && ((Integer.MIN_VALUE + b) > current) ||
                (b < 0 && ((Integer.MAX_VALUE + b) < current)))) {
            return current - b;
        } else {
            throw new OverflowError("Calculation overflow");
        }
    }

    @Override
    protected Integer doThisImpl(Integer a, String operator) {
        return switch (operator) {
            case("+") -> current + a;
            case("-") -> current - a;
            case("*") -> current * a;
            case("/") -> current / a;
            default -> 0;
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
    public Integer getValue() {
        return current;
    }

    protected static MyType<Integer> toType(int exp) {
        return new UncheckedIntegerOperations(exp);
    }
}
