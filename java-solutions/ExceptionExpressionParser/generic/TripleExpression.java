package expression.generic;

/**
 * Three-argument arithmetic expression over integers.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("ClassReferencesSubclass")
public interface TripleExpression<T> extends ToMiniString {


    MyType<T> evaluate(MyType<T> x, MyType<T> y, MyType<T> z, boolean ifChecked);


    //Object evaluate(Object x);
}
