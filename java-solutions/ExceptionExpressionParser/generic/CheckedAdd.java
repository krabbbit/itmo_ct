package expression.generic;

//import expression.Elements;

public class CheckedAdd<T> extends CheckedBinaryOperation<T> {
    public CheckedAdd(abstractGeneralElems<T> a, abstractGeneralElems<T> b) {
        super(a, b);
        this.simbol = '+';
    }

}

