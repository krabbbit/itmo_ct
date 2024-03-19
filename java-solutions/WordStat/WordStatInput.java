import java.lang.*;
import java.io.*;
import java.util.*;

public class WordStatInput {
    public static boolean isWordChar (char simbol) {
    	if ((Character.isLetter(simbol)) || (Character.getType(simbol) == Character.DASH_PUNCTUATION) || (simbol == '\'')) {
    		return true;
    	}
    	return false;
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
			    			//System.out.println(word);
			    			//int search = result.get(word.toString());
			    			if (result.get(word.toString()) != null) {
			    				result.put(word.toString(), result.get(word.toString()) + 1);
			    				//result[word.toString()] += 1;
			    			} else {
			    				result.put(word.toString(), 1);
			    			}
			    			word = new StringBuilder();
			    		}
			    		read = in.read();
			    	}

			    	//result.entrySet().stream().sorted(Map.Entry<String, Integer>comparingByValue().reversed());

			    	for (Map.Entry<String, Integer> pair: result.entrySet()) {
			    		//System.out.println(pair.getKey());
			    		String outputString = pair.getKey() + " " + pair.getValue().toString();
			    		out.write(outputString);
			    		out.newLine();
			    	}

			    } catch (IOException e) {
					System.out.println("Don't read/write");
				} finally {
					in.close();
					out.close();
				}
			} catch (IOException e) {
				System.out.println("Don't open outFile");
			}
		} catch (IOException e) {
			System.out.println("Don't open inFile");
		} 
	}
}