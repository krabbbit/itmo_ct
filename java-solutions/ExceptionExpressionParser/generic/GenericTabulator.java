package expression.generic;

import java.util.Objects;
import java.util.function.Function;

public class GenericTabulator<T> implements Tabulator{
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
//        ExpressionParser<T> parser;
//        if (mode.equals("i")) {
//            parser = new ExpressionParser()<
//        }
        //System.out.println(expression);
        Function<Integer, MyType> func;
        boolean ifChecked = false;
        if (Objects.equals(mode, "i")) {
            func = UncheckedIntegerOperations::toType;
            ifChecked = true;
        } else if (Objects.equals(mode, "d")) {
            func = DoubleOperations::toType;
        } else if (Objects.equals(mode, "u")) {
            func = UncheckedIntegerOperations::toType;
        } else if (Objects.equals(mode, "b")) {
            func = ByteOperations::toType;
        } else if (Objects.equals(mode, "bi")) {
            func = BigIntegerOperations::toType;
        } else {
            func = BoolOperations::toType;
        }
        abstractGeneralElems parser = new ExpressionParser<>().parse(expression, func);
        MyType<T> x, y, z;
        Object[][][] tab = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for(int i = 1; i <= (x2 - x1 + 1); i++) {
            for(int j = 1; j <= (y2 - y1 + 1); j++) {
                for(int k = 1; k <= (z2 - z1 + 1); k++) {
                    try {
                        x = func.apply(x1 + i - 1);
                        y = func.apply(y1 + j - 1);
                        z = func.apply(z1 + k - 1);

                        tab[i - 1][j - 1][k - 1] = parser.evaluate(x, y, z, ifChecked).getValue();
//                        if (Objects.equals(mode, "bool")) {
//                            tab[i - 1][j - 1][k - 1] = Integer.parseInt(Boolean.toString((Boolean) tab[i - 1][j - 1][k - 1]));
//                        }
//                        if (tab[i - 1][j - 1][k - 1] == null) {
//                            tab[i - 1][j - 1][k - 1] = 0;
//                        }
//                        if (Botab[i - 1][j - 1][k - 1] == false)
                    } catch (Exception e) {
                        tab[i - 1][j - 1][k - 1] = null;
                    }
//                    catch (NumberFormatException e) {
//                        x = func.apply(1);
//                        y = func.apply(1);
//                        z = func.apply(1);
//                        tab[i - 1][j - 1][k - 1] = parser.evaluate(x, y, z, ifChecked).getValue();;
//                    }
                }
            }
        }
        return tab;
    }
}
