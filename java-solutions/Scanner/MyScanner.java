import java.lang.*;
import java.io.*;
import java.util.*;

class MyScanner implements AutoCloseable{
	private static char[] buffer = new char[8192];
	private static int bufferIndex = -1;
	private StringBuilder nextWord1 = new StringBuilder(); 
	private Reader myRead;
    private boolean okey = false;
    private int flag = 0;
    private static int lineCount = 1;
    private static String STRING_FLAG = "word";
    private static String INT_FLAG = "int";

	MyScanner (FileReader myRead) {
		this.myRead = myRead;
	}

	MyScanner (String s) {
		this.myRead = new StringReader(s);
	}

	MyScanner (InputStreamReader myRead) {
		this.myRead = myRead;
	}

	public static int getLineCount () {
		return lineCount;
	}

    public static boolean isWordChar (char simbol) {
    	return Character.isLetter(simbol) || (Character.getType(simbol) == Character.DASH_PUNCTUATION) || (simbol == '\'');
    }

    public static boolean isDigitChar (char simbol) {
    	return Character.isDigit(simbol) || (simbol == '-');
    }

    public static int outABC (String str) {
    	String k = "";
    	if(str.toCharArray()[0] == '-') {
    	    str = str.substring(1, str.length());
    	    k = "-";
    	}
    	StringBuilder out = new StringBuilder(k);
    	for (int i = 0; i < str.length(); i++) {
    		out.append(str.toCharArray()[i] - 'a');
    	}
    	if (out.toString().isEmpty()) {
    		return -1;
    	}
    	return Integer.parseInt(out.toString());
    }

    public static String inABC (int p) {
    	String num = String.valueOf(p);
    	String k = "";
    	if(num.toCharArray()[0] == '-') {
    	    num = num.substring(1, num.length());
    	    k = "-";
    	}
    	StringBuilder out = new StringBuilder(k);
    	for (int i = 0; i < num.length(); i++) {
    		out.append((char)((int)num.toCharArray()[i] + (int)'1'));
    	}
    	return out.toString();
    }

    public static boolean isNewLineChar () {
    	if (bufferIndex < 0) {
    		return false;
    	}
    	if (System.lineSeparator().length() > 1) {
    		return (bufferIndex > 0) && ((System.lineSeparator()).charAt(0) == buffer[bufferIndex - 1]) && ((System.lineSeparator()).charAt(1) == buffer[bufferIndex]);
    		//return (bufferIndex > 0) && (System.lineSeparator().contains(Character.toString(buffer[bufferIndex])) && (Boolean.compare((bufferIndex > 0), false) <= Boolean.compare((System.lineSeparator().contains(Character.toString(buffer[bufferIndex - 1]))), false)));
    	}  
    	//return (System.lineSeparator().contains(Character.toString(buffer[bufferIndex])));
    	return (System.lineSeparator()).charAt(0) == buffer[bufferIndex];
    }

    public String nextWord(String what) {
        try {
            if (bufferIndex == -1) {
                flag = myRead.read(buffer);
                bufferIndex = 0;
            }

            if (okey || bufferIndex == 0 && ((what.equals(STRING_FLAG) && !isWordChar(buffer[bufferIndex])) || (what.equals(INT_FLAG) && !isDigitChar(buffer[bufferIndex])))) {
                nextWord1.setLength(0);
                okey = false;
            }

            while (true) {
                if (bufferIndex < flag) {
                    if ((what.equals(STRING_FLAG) && isWordChar(buffer[bufferIndex])) || (what.equals(INT_FLAG) && isDigitChar(buffer[bufferIndex]))) {
                        nextWord1.append(buffer[bufferIndex]);
                    } else if (isNewLineChar()) {
                        lineCount++;
                    } else {
                        okey = true;
                        bufferIndex++;
                        break;
                    }
                    bufferIndex++;
                } else {
                    flag = myRead.read(buffer);
                    bufferIndex = 0;
                    if (flag == -1) {
                        return "/end";
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IOException");
            return "error";
        }

        if (nextWord1.length() == 0) {
            return "/null";
        }
        return nextWord1.toString();
    }


    public String nextLine () { 
    	try {
    		if (bufferIndex == -1) {
    			flag = myRead.read(buffer);
    			bufferIndex = 0;
    		}
	    	if (okey || nextWord1.length() == 0) {
	    		nextWord1.setLength(0);
                okey = false;
	    	}
	    	while (true) {
		    	if (bufferIndex < flag) {
		    		if (buffer[bufferIndex] != '\r' && buffer[bufferIndex] != '\n') { //\r buffer[bufferIndex] != '\n' && buffer[bufferIndex] != '\r'
		    			nextWord1.append(buffer[bufferIndex]);
		    		} else {
                        okey = true;
		    			bufferIndex++;
		    			break;
		    		}
		    		bufferIndex++;
		    	} else {
		    		flag = myRead.read(buffer);
		    		bufferIndex = 0;
		    		if (flag == -1) {
		    			return "/end";
		    		}
		    	}
	    	}
    	} catch (IOException e) {
    		System.err.println("IOException");
    		return "error";
    	}	
    	if (nextWord1.length() == 0) {
    		return "/null";
    	}
		return nextWord1.toString();
    }

    @Override
    public void close(){
    	try {
    		myRead.close();
    	} catch (IOException e) {
    		System.err.println("Close Error");
    	}
    	
    }
}