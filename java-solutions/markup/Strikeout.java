package markup;

import java.util.List;

public class Strikeout extends ElementsAbstract implements inParag {
    public Strikeout(List<ElementsAbstract> list) {
        super(list);
    }

    protected String getTag() {
        return "~";
    }

    protected String getOpenTag() {
        return "[s]";
    }

    protected String getCloseTag() {
        return "[/s]";
    }

}
