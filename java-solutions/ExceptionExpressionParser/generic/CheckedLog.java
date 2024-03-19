//package expression.generic;
//
//import expression.Const;
//import expression.Elements;
//import expression.ListExpression;
//import expression.TripleExpression;
//
//import java.util.List;
//import java.util.Objects;
//
//public class CheckedLog implements TripleExpression, Elements, ListExpression {
//    protected Elements elem1;
//    public CheckedLog(Elements a) {
//        elem1 = a;
//    }
//
//    @Override
//    public String toString() {
//        return "log2(" + elem1.toString() + ")";
//    }
//
//    private void  check (int a) {
//        if (a <= 0) {
//            throw new CalculationException("Negative logarithm argument");
//        }
//    }
//
//    @Override
//    public int evaluate(int x, int y, int z) {
//        int a = elem1.evaluate(x, y, z);
//        check(a);
//        for (int i = 0; i < 31; i++) {
//            int pow = (new CheckedPow(new Const(i))).evaluate(x, y, z);
//            if (pow == a) {
//                return i;
//            } else if (pow > a) {
//                return i - 1;
//            }
//        }
//        return 30;
//    }
//
//    @Override
//    public int evaluate(int x) {
//        int a = elem1.evaluate(x);
//        check(a);
//        for (int i = 0; i <= 31; i++) {
//            if ((new CheckedPow(new Const(i))).evaluate(x) == a) {
//                return i;
//            } else if ((new CheckedPow(new Const(i))).evaluate(x) > a) {
//                return i - 1;
//            }
//        }
//        return 0;
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj != null && this.getClass() != obj.getClass()) {
//            return false;
//        }
//        return obj != null && this.elem1.equals(((CheckedLog) obj).elem1);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(this.toString());
//    }
//
//    @Override
//    public int evaluate(List<Integer> variables) {
//        int a = elem1.evaluate(variables);
//        check(a);
//        for (int i = 0; i <= 31; i++) {
//            if ((new CheckedPow(new Const(i))).evaluate(variables) == a) {
//                return i;
//            } else if ((new CheckedPow(new Const(i))).evaluate(variables) > a) {
//                return i - 1;
//            }
//        }
//        return 0;
//    }
//}
