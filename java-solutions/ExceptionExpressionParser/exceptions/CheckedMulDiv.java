package expression.exceptions;

public class CheckedMulDiv {

    public CheckedMulDiv () {}
    public static void checked (int index, String str, StringBuilder brackets) {
        if (index < str.length() && (str.charAt(index) == ')' || str.charAt(index) == '}' || str.charAt(index) == ']') && brackets.isEmpty()) {
            throw new BracketsException("No open brackets here");
        }  else if (index < str.length() && str.charAt(index) != '+' && str.charAt(index) != '-' && str.charAt(index) != ')' && str.charAt(index) != ']' && str.charAt(index) != '}'){
            throw new ParserException("Expected arithmetic operator here", str, index);
        }
    }

}
