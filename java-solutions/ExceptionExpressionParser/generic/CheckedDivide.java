package expression.generic;

//import expression.Elements;

public class CheckedDivide<T> extends CheckedBinaryOperation<T> {
    public CheckedDivide(abstractGeneralElems<T> a, abstractGeneralElems<T> b) {
        super(a, b);
        this.simbol = '/';
    }
}

