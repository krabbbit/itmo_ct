package expression.parser;

import expression.*;
import expression.exceptions.OverflowError;

public class ExpressionParser implements TripleParser {
    private String str;
    private int index = 0;
    private StringBuilder brackets = new StringBuilder();
    public ExpressionParser () {
    }

    public TripleExpression parse(String str) {
        this.str = str;
        //System.err.println(str);
        TripleExpression tr = null;
        try {
            tr = (TripleExpression) PlusMinus();
        } catch (OverflowError e) {
            System.err.println(e.getMessage());
            return new Variable(e.getMessage());
        }
        index = 0;
        brackets = new StringBuilder();
        if (tr == null) {
            return new Const(0);
        }
        return tr;
    }

    private Expression PlusMinus() {
        skipWhitespace();
        Expression res1 = MulDiv();
        skipWhitespace();
        while (index < str.length()) {
            if (str.charAt(index) == '+') {
                index++;
                Expression res2 = MulDiv();
                res1 = new CheckedAdd((Elements) res1, (Elements) res2);
            } else if (str.charAt(index) == '-') {
                index++;
                Expression res2 = MulDiv();
                res1 = new CheckedSubtract((Elements) res1, (Elements) res2);
            } else {
                CheckedAdSubstruct.checked(index, str, brackets);
                break;
            }
            skipWhitespace();
        }
        return res1;
    }

    private Expression MulDiv() {
        skipWhitespace();
        Expression res1 = Scobochki();
        skipWhitespace();
        while (index < str.length()) {
            if (str.charAt(index) == '*') {
                index++;
                Expression res2 = Scobochki();
                res1 = new CheckedMultiply((Elements) res1, (Elements) res2);
            } else if (str.charAt(index) == '/') {
                index++;
                Expression res2 = Scobochki();
                res1 = new CheckedDivide((Elements) res1, (Elements) res2);
            } else {

                CheckedMulDiv.checked(index, str, brackets);
                break;
            }
            skipWhitespace();
        }
        return res1;
    }

    private Expression Scobochki() {
        skipWhitespace();
        if ((index + 1) < str.length() && (str.charAt(index) == '-') && ((Character.isDigit(str.charAt(index + 1)) || (Character.isLetter(str.charAt(index + 1)))))) {
            return Variables();
        } else if (index < str.length() && (str.charAt(index) == '-')) {
            skipWhitespace();
            index++;
            skipWhitespace();
            if (index < str.length() && str.charAt(index) != '(') {
                try {
                    return new CheckedNegate((Elements) Scobochki());
                } catch (StackOverflowError e) {
                    System.err.println(e.getMessage());
                    return new CheckedNegate(new Const(666));
                }
            } else {
                Expression res = whatIsUnaryMinus(true);
                if (res != null) {
                    return res;
                }
            }
        }
        Expression res = whatIsUnaryMinus(false);
        if (res != null) {
            return res;
        }
        Expression v = Variables();
        return v;
    }

    private Expression Variables() {
        skipWhitespace();
        if (index < str.length() && Character.isLetter(str.charAt(index))) {
            Expression res1 = new Variable(Character.toString(str.charAt(index)));
            index++;
            return res1;
        }
        if (str.isEmpty()) {
            return null;
        }
        if (index >= str.length()) {
            //return null;
            throw new ArithmeticException("ожидается непустой второй аргумент");
        }
        if (!Character.isDigit(str.charAt(index)) && (str.charAt(index) != '-')) {
            throw new ArithmeticException("неизвестное число или переменная");
        }
        StringBuilder s = new StringBuilder(Character.toString(str.charAt(index)));
        index++;
        while (index < str.length() && Character.isDigit(str.charAt(index))) {
            s.append(str.charAt(index));
            index++;
        }
        if (s.toString().equals("-")) {
            return new CheckedNegate((Elements) PlusMinus());
        }
        try {
            return new Const(Integer.parseInt(s.toString()));
        } catch (NumberFormatException e) {
            throw new ArithmeticException("overflow");
        }
    }

    private Expression whatIsUnaryMinus(boolean ee) {
        if (index < str.length() && str.charAt(index) == '(') {
            index++;
            brackets.append("(");
            skipWhitespace();
            Expression res1 = PlusMinus();
            if (ee) {
                res1 = new CheckedNegate((Elements) res1);
            }
            skipWhitespace();
            if (index < str.length() && str.charAt(index) == ')') {
                index++;
                if (!brackets.isEmpty()) {
                    brackets.deleteCharAt(brackets.length() - 1);
                } else {
                    throw new ArithmeticException("закрыли пустоту");
                }
                return res1;
            } else {
                throw new ArithmeticException("нет закрывающейся скобки");
            }
        } else {
            return null;
        }
    }

    private void skipWhitespace() {
        while (index < str.length() && (str.charAt(index) == ' ' || Character.isWhitespace(str.charAt(index)) || str.charAt(index) == '\r' || str.charAt(index) == '\n' || str.charAt(index) == '\t')) {
            index++;
        }
    }
}
