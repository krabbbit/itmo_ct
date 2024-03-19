package expression.generic;

//import expression.Elements;

public class CheckedMultiply<T> extends CheckedBinaryOperation<T> {
    public CheckedMultiply(abstractGeneralElems<T> a, abstractGeneralElems<T> b) {
        super(a, b);
        this.simbol = '*';
    }

}

