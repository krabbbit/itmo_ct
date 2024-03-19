package expression.generic;


import java.util.Objects;

public class BoolOperations extends abstractTypes<Boolean>{
    public BoolOperations(Boolean cur) {
        super(cur);
    }

    @Override
    protected MyType<Boolean> getTypeCurrent(Boolean current) {
        return new BoolOperations(current);
    }

    public static MyType<Boolean> toType(int exp) {
        if (exp == 0) {
            return new BoolOperations(false);
        } else {
            return new BoolOperations(true);
        }
    }

    @Override
    protected Boolean check(String simbol, Boolean b, boolean isCheck) {
        return false;
    }

    @Override
    protected Boolean doThisImpl(Boolean a, String operator) {
        if (Objects.equals(operator, "/") && !a) {
            throw new ZeroDivisionError();
        }
        return switch (operator) {
            case("+") -> Boolean.logicalOr(current, a);
            case("-") -> Boolean.logicalXor(current, a);
            default -> Boolean.logicalAnd(current, a);
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
    public Boolean getValue() {
        return current;
    }
}
