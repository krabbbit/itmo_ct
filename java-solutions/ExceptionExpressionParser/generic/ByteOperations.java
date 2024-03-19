package expression.generic;

public class ByteOperations extends abstractTypes<Byte>{
    public ByteOperations(Byte cur) {
        super(cur);
    }

    @Override
    protected MyType<Byte> getTypeCurrent(Byte current) {
        return new ByteOperations(current);
    }

    public static MyType<Byte> toType(String exp) {
        return new ByteOperations(Byte.parseByte(exp));
    }

    public static MyType<Byte> toType(int exp) {
        return new ByteOperations((byte) exp);
    }

    @Override
    protected Byte check(String simbol, Byte b, boolean isCheck) {
        return 0;
    }

    @Override
    protected Byte doThisImpl(Byte a, String operator) {
        return switch (operator) {
            case("+") -> (byte)((int)current + (int)a);
            case("-") -> (byte)((int)current - (int)a);
            case("*") -> (byte)((int)current * (int)a);
            default -> (byte)((int)current / (int)a);
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
    public Byte getValue() {
        return current;
    }
}
