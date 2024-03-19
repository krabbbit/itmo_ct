package expression.generic;

import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface TripleParser<T> {
    Expression<T> parse(String expression, Function<Integer, MyType> f) throws Exception;
}
