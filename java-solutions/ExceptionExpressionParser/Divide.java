package expression;

public class Divide extends BinaryOperation implements Elements{

    public Divide(Elements a, Elements b) {
        super(a, b);
        this.simbol = '/';
    }
}
