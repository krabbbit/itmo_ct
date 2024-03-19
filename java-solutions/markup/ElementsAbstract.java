package markup;

import java.util.List;

public class ElementsAbstract implements Markdown, BBCode {
    protected String text;
    private List<ElementsAbstract> list;

    public ElementsAbstract(List<ElementsAbstract> list) {
        this.list = list;
    }

    public ElementsAbstract(String text) {
        this.text = text;
    }

    protected String getTag() {
        return "";
    }

    protected String getOpenTag() {
        return "";
    }

    protected String getCloseTag() {
        return "";
    }

    @Override
    public void toMarkdown(StringBuilder result) {
        result.setLength(0);
        result.append(getTag());
        StringBuilder k = new StringBuilder();
        for (ElementsAbstract elem : this.list) {
            elem.toMarkdown(k);
            result.append(k);
        }
        result.append(getTag());
        // result.append(SEP);
        //return result;
    }

    @Override
    public void toBBCode(StringBuilder result) {
        result.setLength(0);
        // result.append(SEP);
        result.append(getOpenTag());
        StringBuilder k = new StringBuilder();
        for (ElementsAbstract elem : this.list) {
            elem.toBBCode(k);
            result.append(k);
        }
        result.append(getCloseTag());
    }
}
