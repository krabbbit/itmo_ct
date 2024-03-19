import java.lang.*;
import java.io.*;
import java.util.*;

public class WsppSortedPosition {

	public static void main (String[] args) {

		String inFile = args[0];
		String outFile = args[1];
		String word;
		Map<String, LinkedList<String>> result = new TreeMap<String, LinkedList<String>>();
		List<String> arrayWords = new LinkedList<String>();
		int indexLine = 0;
		int index = 1;
		String newLineWord = "";

		try {
			MyScanner scLine = new MyScanner(new InputStreamReader(new FileInputStream(inFile) , "UTF-8"));
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile),
					"UTF-8"
			    ));
			    try {
			    	int k = MyScanner.getLineCount();
			    	word = scLine.nextWord("word").toLowerCase();; // line not in memory 
			    	while (!Objects.equals(word, "/end")) {
			    		if (!Objects.equals(word, "/null")) {
			    			arrayWords = new LinkedList<String>();
			    			if (!newLineWord.equals("/end") && !newLineWord.equals("/null") && !newLineWord.equals("")) {
			                    arrayWords.add(newLineWord);
			                }
			    			while (k == MyScanner.getLineCount() && !Objects.equals(word, "/end")) {
			    				if (!Objects.equals(word, "/null")) { 
				    				arrayWords.add(word);
				    			}
				    			word = scLine.nextWord("word").toLowerCase();
							} 
							if (k != MyScanner.getLineCount()) {
								indexLine++;
							}


							newLineWord = String.valueOf(word);
							for (int i = 0; i < arrayWords.size(); i++) {
								word = arrayWords.get(i);
								if (result.get(word) != null) {
			    					String currentValue = result.get(word).get(0);
			    					result.get(word).set(0, Integer.toString(Integer.parseInt(currentValue) + 1));
        							result.get(word).add(Integer.toString(indexLine) + ":" + Integer.toString(arrayWords.size() - i));
        						} else {
			    					result.put(word, new LinkedList<String>(Arrays.asList("1", Integer.toString(indexLine) + ":" + Integer.toString(arrayWords.size() - i))));
			    				}
							}
							k = MyScanner.getLineCount();
						}
						word = scLine.nextWord("word").toLowerCase();
			    	}

			    	for (Map.Entry<String, LinkedList<String>> pair: result.entrySet()) {
			    		//System.out.println(pair.getKey());
			    		StringBuilder outputString = new StringBuilder(pair.getKey() + " ");
			    		for (int i = 0; i < pair.getValue().size() - 1; i++) {
			    			outputString.append((pair.getValue().get(i)) + " ");
			    		}
			    		outputString.append((pair.getValue().peekLast()));
			    		out.write(outputString.toString());
			    		//System.err.println(outputString.toString());
			    		out.newLine();
			    	}

			    } catch (IOException e) {
					System.err.println("Error when working with data" + e.getStackTrace());
				} finally {
					out.close();
				}
			} catch (IOException e) {
				System.err.println("Don't open outFile" + e.getStackTrace());
			} finally {
				scLine.close();
			}
		} catch (IOException e) {
			System.err.println("Don't open inFile" + e.getStackTrace());
		}

	}
}