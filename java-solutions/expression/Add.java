package expression;

public class Add extends BinaryOperation implements Elements{

    public Add(Elements a, Elements b) {
        super(a, b);
        this.simbol = '+';
    }

}
