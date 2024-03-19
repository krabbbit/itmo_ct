//package expression;
//
//import expression.generic.MyType;
//
//import java.util.List;
//import java.util.Objects;
//
//public abstract class BinaryOperation<T extends Number> implements Elements<T>, TripleExpression<T>, ListExpression<T>{
//    protected char simbol;
//    public TripleExpression<T> elem1;
//    public TripleExpression<T> elem2;
//
//    public BinaryOperation (TripleExpression<T> a, TripleExpression<T> b) {
//        elem1 = a;
//        elem2 = b;
//    }
//
////    @Override
////    public T evaluate(T x) {
////        return switch (simbol) {
////            case '*' -> elem1.evaluate(x).intValue() * elem2.evaluate(x).intValue();
////            case '/' -> elem1.evaluate(x) / elem2.evaluate(x);
////            case '-' -> elem1.evaluate(x) - elem2.evaluate(x);
////            case '+' -> elem1.evaluate(x) + elem2.evaluate(x);
////            default -> 0;
////        };
////    }
//
//    public T add(T other) {
//        if (other instanceof Integer) {
//            return (T) ((Integer) other + (Integer) other);
//        } else if (operand1 instanceof Double) {
//            return (T) ((Double) operand1 + (Double) other);
//        } else if (operand1 instanceof BigInteger) {
//            return (T) ((BigInteger) operand1).add((BigInteger) other);
//        } else {
//            throw new UnsupportedOperationException("Unsupported type: " + operand1.getClass().getName());
//        }
//    }
//    @Override
//    public T evaluate(List<T> variables) {
//        return switch (simbol) {
//            case '*' -> elem1.evaluate(variables) * elem2.evaluate(variables);
//            case '/' -> elem1.evaluate(variables) / elem2.evaluate(variables);
//            case '-' -> elem1.evaluate(variables) - elem2.evaluate(variables);
//            case '+' -> elem1.evaluate(variables) + elem2.evaluate(variables);
//            default -> 0;
//        };
//    }
//    @Override
//    public T evaluate(T x, T y, T z) {
//        if
//        return switch (simbol) {
//            case '*' -> elem1.evaluate(x, y, z) * elem2.evaluate(x, y, z);
//            case '/' -> elem1.evaluate(x, y, z) / elem2.evaluate(x, y, z);
//            case '-' -> elem1.evaluate(x, y, z) - elem2.evaluate(x, y, z);
//            case '+' -> elem1.evaluate(x, y, z) + elem2.evaluate(x, y, z);
//            default -> 0;
//        };
//    }
//}
