package expression;

public class Multiply extends BinaryOperation implements Elements{

    public Multiply(Elements a, Elements b) {
        super(a, b);
        this.simbol = '*';
    }

}
