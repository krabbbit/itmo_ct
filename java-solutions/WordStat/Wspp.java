import java.lang.*;
import java.io.*;
import java.util.*;

public class Wspp {
	public static void main (String[] args) {
//for commit
		String inFile = args[0];
		String outFile = args[1];
		String word;
		LinkedHashMap<String, LinkedList<Integer>> result = new LinkedHashMap<String, LinkedList<Integer>>();
		LinkedList<Integer> array = new LinkedList<Integer>();
		int index = 1;

		try {
			MyScanner sc = new MyScanner(new InputStreamReader(new FileInputStream(inFile) , "UTF-8"));
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile),
					"UTF-8"
			    ));
			    try {
			    	String read = sc.nextWord("word");
			    	while (!Objects.equals(read, "/end")) {
			    		if (!Objects.equals(read, "/null")) {
			    			word = read.toLowerCase();
			    			if (result.get(word) != null) {
			    				//result.get(word).get(0)++;
			    				int currentValue = result.get(word).get(0);
        						result.get(word).set(0, currentValue + 1);
			    			} else {
			    				result.put(word, new LinkedList<Integer>(Arrays.asList(1)));
			    			}
			    			result.get(word).add(index);
			    			index++;
			    		}
			    		read = sc.nextWord("word");
			    	}
			    	// strArray = result.keySet().toArray(new String[0]);
			    	// intArray = result.values().toArray(new Integer[0]);
			    	// bubbleSort();

			    	for (Map.Entry<String, LinkedList<Integer>> pair: result.entrySet()) {
			    		//System.out.println(pair.getKey());
			    		StringBuilder outputString = new StringBuilder(pair.getKey() + " ");
			    		for (int i = 0; i < pair.getValue().size() - 1; i++) {
			    			outputString.append(Integer.toString(pair.getValue().get(i)) + " ");
			    		}
			    		outputString.append(Integer.toString(pair.getValue().peekLast()));
			    		out.write(outputString.toString());
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
				sc.close();
			}
		} catch (IOException e) {
			System.err.println("Don't open inFile");
		}

	}
}