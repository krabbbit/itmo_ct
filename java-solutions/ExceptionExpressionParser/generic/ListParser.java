package expression.generic;

import java.util.List;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface ListParser<T> {
    ListExpression<T> parse(String expression, final List<String> variables, Function<Integer, MyType> f) throws Exception;
}
