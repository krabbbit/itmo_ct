package expression.generic;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("ClassReferencesSubclass")
@FunctionalInterface
public interface ListExpression<T> extends ToMiniString {

    MyType<T> evaluate(List<MyType<T>> variables, boolean ifChecked);

}
