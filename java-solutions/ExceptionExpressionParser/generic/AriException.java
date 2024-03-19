package expression.generic;

public class AriException extends ArithmeticException{
    public AriException (String s, String str, int index) {
        super(s + "::  " + str.substring(0, index) + " --> " + str.charAt(index) + " <-- " + str.substring(index + 1));
    }
}


