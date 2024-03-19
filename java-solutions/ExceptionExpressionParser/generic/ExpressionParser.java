package expression.generic;

import java.util.List;
import java.util.function.Function;


public class ExpressionParser<T> implements TripleParser<T>, ListParser<T> {
    private String str;
    private int index;
    private StringBuilder brackets;
    private List<String> list;
    private boolean isList = false;
    Function<Integer, MyType> parser;
    MyType<T> un;
    public ExpressionParser() {
    }

    public abstractGeneralElems<T> parse(String str, Function<Integer, MyType> func) {
        parser = func;
        this.str = str;
        un = parser.apply(-1);
        index = 0;
        brackets = new StringBuilder();
        //System.err.println(str);
        abstractGeneralElems<T> tr = PlusMinus();
//        if (tr == null) {
//            return new Const(0);
//        }
        isList = false;
        list = List.of();
        return tr;
    }

    @Override
    public abstractGeneralElems<T> parse(String expression, List<String> variables, Function<Integer, MyType> f) {
        list = variables;
        isList = true;
        return parse(expression, f);
    }

    private abstractGeneralElems<T> PlusMinus() {
        skipWhitespace();
        abstractGeneralElems<T> res1 = MulDiv();
        skipWhitespace();
        while (index < str.length()) {
            if (str.charAt(index) == '+') {
                index++;
                abstractGeneralElems<T> res2 = MulDiv();
                res1 = new CheckedAdd<T>(res1, res2);
            } else if (str.charAt(index) == '-') {
                index++;
                abstractGeneralElems<T> res2 = MulDiv();
                res1 = new CheckedSubtract<T>(res1, res2);
            } else {
                CheckedAddSubstruct.checked(index, str, brackets);
                break;
            }
            skipWhitespace();
        }
        return res1;
    }

    private abstractGeneralElems<T> MulDiv() {
        skipWhitespace();
        abstractGeneralElems<T> res1 = UnaryMinusBrackets();
        skipWhitespace();
        while (index < str.length()) {
            if (str.charAt(index) == '*') {
                index++;
                abstractGeneralElems<T> res2 = UnaryMinusBrackets();
                res1 = new CheckedMultiply<T>(res1, res2);
            } else if (str.charAt(index) == '/') {
                index++;
                abstractGeneralElems<T> res2 = UnaryMinusBrackets();
                res1 = new CheckedDivide<T>(res1, res2);
            } else {
                CheckedMulDiv.checked(index, str, brackets);
                break;
            }
            skipWhitespace();
        }
        return res1;
    }

    private abstractGeneralElems<T> UnaryMinusBrackets() {
        skipWhitespace();
        if ((index + 1) < str.length() && (str.charAt(index) == '-') && ((Character.isDigit(str.charAt(index + 1)) || (Character.isLetter(str.charAt(index + 1)))))) {
            //минус прямо перед числом или переменной
            return Lexems();
        } else if (index < str.length() && (str.charAt(index) == '-')) {
            // после минуса идут пробелы
            skipWhitespace();
            index++;
            skipWhitespace();
            if (index < str.length() && !isOpenBrackets(str.charAt(index))) {
                // если нет скобок - это унарный минус, запускаем рекурсию
                return new UnaryMinus<>(UnaryMinusBrackets(), un);
            } else {
                // если есть скобки - парсим скобки с минусом
                abstractGeneralElems<T> res = parseBrackets(true);
                if (res != null) {
                    return res;
                }
            }
        }
        // минуса нет, парсим скобки без минуса
        abstractGeneralElems<T> res = parseBrackets(false);
        if (res != null) {
            return res;
        }
        // если что-то идет не так - это точно лексема
        return Lexems();
    }

    private abstractGeneralElems<T> Lexems() {
        skipWhitespace();
        StringBuilder s = new StringBuilder();
        if (isList && !Character.isDigit(str.charAt(index)) && str.charAt(index) != '-') {
            while (index < str.length() && !isWhiteSpace(str.charAt(index)) && !isOperationSymbol(str.charAt(index))) {
                s.append(str.charAt(index));
                index++;
            }
            int indexOfVar = list.indexOf(s.toString());
            if (indexOfVar >= 0) {
                Variable<T> res1 = new Variable<T>(indexOfVar);
                res1.setName(list.get(indexOfVar));
                return res1;
            }
        }
        if (index < str.length() && (str.charAt(index) == 'x' || str.charAt(index) == 'y' || str.charAt(index) == 'z')) {
            abstractGeneralElems<T> res1 = new Variable<T>(Character.toString(str.charAt(index)));
            index++;
            return res1;
        }
//        if (str.isEmpty()) {
//            return null;
//        }
        if (index >= str.length()) {
            throw new BoundsException("Expected to continue but the line ended");
        }
//        if (str.charAt(index) == 'l') {
//            return parseLog();
//        }
//        if (str.charAt(index) == 'p') {
//            return parsePow();
//        }
//        if (!Character.isDigit(str.charAt(index)) && (str.
//                charAt(index) != '-')) {
//            throw new ParserException("Expected a variable, const or brackets, but got another character here", str, index);
//        }
        s = new StringBuilder(Character.toString(str.charAt(index)));
        index++;
        while (index < str.length() && Character.isDigit(str.charAt(index))) {
            s.append(str.charAt(index));
            index++;
        }
        if (s.toString().equals("-")) {
            return new UnaryMinus<>(PlusMinus(), un);
        }
        try {
            MyType<T> constant = parser.apply(Integer.parseInt(s.toString()));
            return new Const<>(constant);
        } catch (NumberFormatException e) {
            throw new OverflowError("Overflow const");
        }
    }

//    private Expression parsePow() {
//        index++;
//        int i = 0;
//        String expect = "ow2";
//        while (index < str.length() && i < 3) {
//            if (str.charAt(index) != expect.charAt(i)) {
//                throw new ParserException("expected pow2 but it was not received", str, index);
//            }
//            index++;
//            i++;
//        }
//        if (index < str.length() && !isWhiteSpace(str.charAt(index)) && !isOpenBrackets(str.charAt(index))) {
//            throw new AriException("Invalid argument for exponentiation", str, index);
//        }
//        return new CheckedPow((Elements) UnaryMinusBrackets());
//    }

//    private Expression parseLog() {
//        index++;
//        int i = 0;
//        String expect = "og2";
//        while (index < str.length() && i < 3) {
//            if (str.charAt(index) != expect.charAt(i)) {
//                throw new ParserException("expected log2 but it was not received", str, index);
//            }
//            index++;
//            i++;
//        }
//        if (index < str.length() && !isWhiteSpace(str.charAt(index)) && !isOpenBrackets(str.charAt(index))) {
//            throw new AriException("Invalid argument for logarithm", str, index);
//        }
//        return new CheckedLog((Elements) UnaryMinusBrackets());
//    }

    private abstractGeneralElems<T> parseBrackets(boolean ee) {
        if (index < str.length() && isOpenBrackets(str.charAt(index))) {
            brackets.append(str.charAt(index));
            index++;
            skipWhitespace();
            abstractGeneralElems<T> res1 = PlusMinus();
            if (ee) {
                res1 = new UnaryMinus<>(res1, un);
            }
            skipWhitespace();
            if (index < str.length() && (isCloseBrackets(str.charAt(index)))) {
                if (index < str.length() && !brackets.isEmpty() && (str.charAt(index) == ')' && brackets.charAt(brackets.length() - 1) == '(' ||
                        str.charAt(index) == ']' && brackets.charAt(brackets.length() - 1) == '[' ||
                        str.charAt(index) == '}' && brackets.charAt(brackets.length() - 1) == '{')) {
                    brackets.deleteCharAt(brackets.length() - 1);
                    index++;
                } else {
                    throw new BracketsException("No open brackets ");
                }
                return res1;
            } else {
                throw new BracketsException("No closing bracket");

            }
        } else {
            return null;
        }
    }
    private boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c);
    }

    private boolean isOperationSymbol(char c) {
        return (c == '+' || c == '-' || c == '/' || c == '*' || c == ')' || c == ']' || c == '}');
    }

    private void skipWhitespace() {
        while (index < str.length() && isWhiteSpace(str.charAt(index))) {
            index++;
        }
    }
    private boolean isOpenBrackets (char c) {
        return c == '(' || c == '[' || c == '{';
    }
    private boolean isCloseBrackets (char c) {
        return c == ')' || c == ']' || c == '}';
    }

}
