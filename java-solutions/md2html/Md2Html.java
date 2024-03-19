package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class MyPair {
    int c = 0;
    private final String first;
    private final int second;

    public MyPair(String first, int second) {
        this.first = first;
        this.second = second;
    }

    public String first() {
        return first;
    }

    public int second() {
        return second;
    }
}


public class Md2Html {
    int c = 0;
    private final Set<String> STRS = Set.of("*", "**", "_", "__", "--", "`", "%", "\\");
    public StringBuilder parts = new StringBuilder();
    public List<String> parags = new ArrayList<>();

    private StringBuilder result = new StringBuilder();

    public static boolean isTagSymbol(char symbol) {
        return symbol == '*' || symbol == '_' || symbol == '-' || symbol == '`' || symbol == '<' || symbol == '>' || symbol == '&' || symbol == '%' || symbol == '\\';
    }

    public boolean isTag(String str) {
        return STRS.contains(str);
    }

    public void addPartsOfParag(char symbol) {
        if (isTagSymbol(symbol)) {
            if (!parts.isEmpty()) {
                parags.add(parts.toString());
                parts.setLength(0);
            }
            parts.append(symbol);
            if (!parags.isEmpty() && parts.toString().equals(parags.get(parags.size() - 1))) {
                parags.set(parags.size() - 1, parts + parts.toString());
            } else {
                parags.add(parts.toString());
            }
            parts.setLength(0);
        } else {
            parts.append(symbol);
        }
    }

    public void writeParag() {
        for (String s : parags) {
            if (!s.isEmpty()) {
                if (!s.equals("\\")) {
                    //out.write(s);
                    result.append(s);
                }
            }
        }
    }

    public void createParag() {
        Deque<MyPair> stack = new LinkedList<>();
        HashMap<String, String> openTag = new HashMap<>();
        openTag.put("*", "<em>");
        openTag.put("_", "<em>");
        openTag.put("**", "<strong>");
        openTag.put("__", "<strong>");
        openTag.put("--", "<s>");
        openTag.put("`", "<code>");
        openTag.put("%", "<var>");

        HashMap<String, String> closeTag = new HashMap<>();
        closeTag.put("*", "</em>");
        closeTag.put("_", "</em>");
        closeTag.put("**", "</strong>");
        closeTag.put("__", "</strong>");
        closeTag.put("--", "</s>");
        closeTag.put("`", "</code>");
        closeTag.put("%", "</var>");
        int n = parags.size();
        for (int i = 0; i < n; i++) {
            String str = parags.get(i);
            if (isTag(str)) {
                MyPair pair = new MyPair(str, i);
                stack.push(pair);
                if (stack.size() > 1) {
                    String last = stack.pop().first();
                    if (last.equals(stack.peek().first())) {
                        if (parags.contains(last)) {
                            int index2 = stack.peek().second();
                            parags.set(index2, openTag.get(last));
                            parags.set(i, closeTag.get(last));
                        }
                        stack.pop();
                    } else {
                        stack.push(new MyPair(last, i));
                    }
                }
            }
            switch (str) {
                case "<" -> parags.set(i, "&lt;");
                case ">" -> parags.set(i, "&gt;");
                case "&" -> parags.set(i, "&amp;");
            }
            if (str.equals("\\")) {
                parags.set(i, "");
            }
        }
    }


    public static void main(String[] args) {
        String inFile = args[0];
        String outFile = args[1];
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inFile), StandardCharsets.UTF_8));
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outFile),
                        StandardCharsets.UTF_8
                ));
                try {
                    //String line = in.readLine();
                    String line;
                    boolean inH = false;
                    boolean inP = false;
                    int countH = 0;
                    StringBuilder beginStr = new StringBuilder();
                    Md2Html obj = new Md2Html();

                    while ((line = in.readLine()) != null) {


                        int i = 0;
                        beginStr.setLength(0);
                        for (char symbol : line.toCharArray()) {
                            if (!inH && !inP) {
                                if (i == 0) {
                                    if (symbol == '#') {
                                        countH++;
                                        beginStr.append(symbol);
                                    } else {
                                        inP = true;
                                        //out.write("<p>");
                                        obj.result.append("<p>");
                                        obj.addPartsOfParag(symbol);
                                    }
                                } else if (Character.isSpaceChar(symbol)) {
                                    if (countH != 0) {
                                        inH = true;
                                        //out.write("<h" + countH + ">");
                                        obj.result.append("<h" + countH + ">");
                                    }
                                } else {
                                    if (symbol == '#') {
                                        countH++;
                                    } else if (countH != 0 && !Character.isSpaceChar(symbol)) {
                                        inP = true;
                                        //out.write("<p>");
                                        obj.result.append("<p>");
                                        if (!beginStr.isEmpty()) {
                                            for (char k : beginStr.toString().toCharArray()) {
                                                obj.addPartsOfParag(k);
                                            }
                                        }
                                        obj.addPartsOfParag(symbol);
                                        countH = 0;
                                    } else {
                                        obj.addPartsOfParag(symbol);
                                    }
                                }
                            } else if (inH) {
                                if (i == 0) {
                                    obj.addPartsOfParag('\n');
                                }
                                obj.addPartsOfParag(symbol);
                            } else {
                                if (i == 0) {
                                    obj.addPartsOfParag('\n');
                                }
                                obj.addPartsOfParag(symbol);
                            }

                            i++;
                        }
                        if (i == 0) {
                            if (inH) {
                                if (!obj.parts.isEmpty()) {
                                    obj.parags.add(obj.parts.toString());
                                }
                                obj.createParag();
                                obj.writeParag();
                                //out.write("</h" + countH + ">\n");
                                obj.result.append("</h" + countH + ">\n");
                                countH = 0;
                                inH = false;
                                obj.parags.clear();
                                obj.parts.setLength(0);
                            } else if (inP) {
                                if (!obj.parts.isEmpty()) {
                                    obj.parags.add(obj.parts.toString());
                                }
                                obj.createParag();
                                obj.writeParag();
                                //out.write("</p>\n");
                                obj.result.append("</p>\n");
                                inP = false;
                                obj.parags.clear();
                                obj.parts.setLength(0);
                            }
                        }
                    }
                    if (!obj.parts.isEmpty()) {
                        obj.parags.add(obj.parts.toString());
                    }
                    obj.createParag();
                    obj.writeParag();
                    obj.parags.clear();
                    obj.parts.setLength(0);
                    if (inH) {
                        obj.result.append("</h" + countH + ">\n");
                        //out.write("</h" + countH + ">\n");
                    } else if (inP) {
                        obj.result.append("</p>\n");
                        //out.write("</p>\n");
                    }
                    try {
                        out.write(obj.result.toString());
                    } catch (IOException e) {
                        System.out.println("Don't write" + e.getMessage());
                    } finally {
                        out.close();
                        //out.close();
                    }


                } catch (IOException e) {
                    //Todo split on in/out exception
                    System.out.println("Don't read" + e.getMessage());
                } finally {
                    in.close();
                    //out.close();
                }
            } catch (IOException e) {
                System.out.println("Don't open outFile" + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Don't open inFile" + e.getMessage());
        }
    }

}
