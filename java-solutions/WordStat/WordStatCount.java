import java.lang.*;
import java.io.*;
import java.util.*;

public class WordStatCount {
	public static String[] strArray;
	public static Integer[] intArray;

    public static void bubbleSort () {
        boolean isSorted = false;
        Integer buffer1;
        String buffer2;
        while (!isSorted) {
            isSorted = true;
            for (int i = 0; i < intArray.length - 1; i++) {
                if (intArray[i] > intArray[i + 1]) {
                    isSorted = false;
                    buffer1 = intArray[i];
                    buffer2 = strArray[i];
                    intArray[i] = intArray[i + 1];
                    intArray[i + 1] = buffer1;
                    strArray[i] = strArray[i + 1];
                    strArray[i + 1] = buffer2;
                }
            }
        }
    }

	public static void main (String[] args) {
		String inFile = args[0];
		String outFile = args[1];
		StringBuilder word = new StringBuilder();
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(inFile),
				"UTF-8"
			));
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile),
					"UTF-8"
			    ));
			    try {
			    	int read = in.read();
			    	while (read >= 0) {
			    		char simbol = (char)read;
			    		if ((Character.isLetter(simbol)) || (Character.getType(simbol) == Character.DASH_PUNCTUATION) || (simbol == '\'')) {
			    			word.append((Character.toLowerCase((char)read)));
			    		} else if (word.length() != 0) {
			    			if (result.get(word.toString()) != null) {
			    				result.put(word.toString(), result.get(word.toString()) + 1);
			    			} else {
			    				result.put(word.toString(), 1);
			    			}
			    			word = new StringBuilder();
			    		}
			    		read = in.read();
			    	}
			    	strArray = result.keySet().toArray(new String[0]);
			    	intArray = result.values().toArray(new Integer[0]);
			    	bubbleSort();

			    	for (int i = 0; i < result.size(); i++) {
			    		String outputString = strArray[i] + " " + intArray[i].toString();
			    		//System.err.println(strArray[i] + " " + intArray[i]);
			    		out.write(outputString);
			    		out.newLine();
			    	}

			    } catch (IOException e) {
					System.err.println("Don't read/write");
				} finally {
					out.close();
				}
			} catch (IOException e) {
				System.err.println("Don't open outFile");
			} finally {
				in.close();
			}
		} catch (IOException e) {
			System.err.println("Don't open inFile");
		}

	}
}