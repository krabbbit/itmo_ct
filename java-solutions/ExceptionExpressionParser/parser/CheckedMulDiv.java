package expression.parser;

public class CheckedMulDiv {

    public CheckedMulDiv () {}
    public static void checked (int index, String str, StringBuilder brackets) {
        if (str.charAt(index) == ')' && brackets.isEmpty()) {
            throw new ArithmeticException("закрыли пустоту");
        }  else if (str.charAt(index) != '+' && str.charAt(index) != '-' && str.charAt(index) != '(' && str.charAt(index) != ')'){
            throw new ArithmeticException("вызвали */, а * или / нету");
        }
    }

}
