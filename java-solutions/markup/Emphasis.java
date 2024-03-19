package markup;

import java.util.List;

public class Emphasis extends ElementsAbstract implements inParag {
    public Emphasis(List<ElementsAbstract> list) {
        super(list);
        //setSEP("*");
    }

    protected String getTag() {
        return "*";
    }

    protected String getOpenTag() {
        return "[i]";
    }

    protected String getCloseTag() {
        return "[/i]";
    }

}
