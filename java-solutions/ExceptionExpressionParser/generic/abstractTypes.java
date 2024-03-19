package expression.generic;

public abstract class abstractTypes<T> implements MyType<T>{
    protected T current;

    public abstractTypes(T current) {
        this.current = current;
    }

    @Override
    public MyType<T> calc(String oper, T a, boolean isCheck) {
        if (isCheck) {
            return getTypeCurrent(check(oper, a, isCheck));
        }
        return getTypeCurrent(doThisImpl(a, oper));
    }

    @Override
    public MyType<T> add(T a) {
        return getTypeCurrent(doThisImpl(a, "*"));
    }

    protected abstract T doThisImpl(T a, String operator);

    @Override
    public MyType<T> sub(T a) {
        return getTypeCurrent(doThisImpl(a, "-"));
    }

    @Override
    public MyType<T> div(T a) {
        return getTypeCurrent(doThisImpl(a, "/"));
    }

    @Override
    public MyType<T> mul(T a) {
        return getTypeCurrent(doThisImpl(a, "*"));
    }

//    @Override
//    public static MyType<T> toType(String exp) {
//        return toTypeImpl(exp);
//    }

    protected abstract MyType<T> getTypeCurrent(T current);
    //protected abstract MyType<T> toTypeImpl(String exp);
    protected abstract T check(String simbol, T current, boolean isCheck);
}
