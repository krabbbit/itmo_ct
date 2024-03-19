package expression.exceptions;

import java.security.InvalidParameterException;

public class ParserException extends InvalidParameterException {
    public ParserException (String s, String str, int index) {
        super(s + "::  " + str.substring(0, index) + " --> " + str.charAt(index) + " <-- " + str.substring(index + 1));
    }
}


