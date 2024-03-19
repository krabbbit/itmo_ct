package expression.generic;

//import expression.Elements;

public class CheckedSubtract<T> extends CheckedBinaryOperation<T> {
    public CheckedSubtract(abstractGeneralElems<T> a, abstractGeneralElems<T> b) {
        super(a, b);
        this.simbol = '-';
    }

}

