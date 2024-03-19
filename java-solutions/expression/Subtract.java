package expression;

public class Subtract extends BinaryOperation implements Elements{

    public Subtract(Elements a, Elements b) {
        super(a, b);
        this.simbol = '-';
    }

}
