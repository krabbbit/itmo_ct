package expression.generic;

public interface MyType <T> {
    T getValue();
    MyType<T> add(T a);
    MyType<T> sub(T a);
    MyType<T> div(T a);
    MyType<T> mul(T a);

   // MyType<T> toType(String exp);

    MyType<T> calc(String oper, T a, boolean isCheck);
}
