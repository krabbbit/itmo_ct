package expression.generic;

import java.math.BigInteger;

public class BigIntegerOperations extends abstractTypes<BigInteger>{
    public BigIntegerOperations(BigInteger cur) {
        super(cur);
    }

    @Override
    protected MyType<BigInteger> getTypeCurrent(BigInteger current) {
        return new BigIntegerOperations(current);
    }

    public static MyType<BigInteger> toType(int exp) {
        return new BigIntegerOperations(new BigInteger(String.valueOf(exp)));
    }

    @Override
    protected BigInteger check(String simbol, BigInteger b, boolean isCheck) {
        return new BigInteger(String.valueOf(0));
    }

    @Override
    protected BigInteger doThisImpl(BigInteger a, String operator) {
        return switch (operator) {
            case("+") -> current.add(a);
            case("-") -> current.subtract(a);
            case("*") -> current.multiply(a);
            case("/") -> current.divide(a);
            default -> new BigInteger(String.valueOf(0));
        };
    }

    @Override
    public BigInteger getValue() {
        return current;
    }
}
