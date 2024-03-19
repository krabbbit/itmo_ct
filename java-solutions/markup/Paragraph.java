package markup;

import java.util.List;

public class Paragraph implements PLInterface, Markdown {
    private final List<inParag> list;

    public Paragraph(List<inParag> list) {
        this.list = list;
    }

    @Override
    public void toMarkdown(StringBuilder result) {
        result.setLength(0);
        // result.append(SEP);
        StringBuilder k = new StringBuilder();
        for (inParag elem : this.list) {
            elem.toMarkdown(k);
            result.append(k);
        }
    }

    @Override
    public void toBBCode(StringBuilder result) {
        result.setLength(0);
        // result.append(SEP);
        StringBuilder k = new StringBuilder();
        for (inParag elem : this.list) {
            elem.toBBCode(k);
            result.append(k);
        }
    }
}
