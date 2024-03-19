package markup;

import java.util.List;

public class TEST {
    public static void main(String[] args) {
        Paragraph paragraph = new Paragraph(List.of(
                new Strong(List.of(
                        new Text("1"),
                        new Strikeout(List.of(
                                new Text("2"),
                                new Emphasis(List.of(
                                        new Text("3"),
                                        new Text("4")
                                )),
                                new Text("5")
                        )),
                        new Text("6")
                ))
        ));
        //Paragraph paragraph = new Paragraph(List.of(new Strong(new Text("abacaba"))));
        StringBuilder res = new StringBuilder();
        paragraph.toMarkdown(res);
        System.out.println(res);
//        Strong t = new Strong(List.of(new Text("1"), new Text("2")));
//        t.toMarkdown(res);
//        System.out.println(res);
    }
}
