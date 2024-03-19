package markup;

import java.util.List;

public class Strong extends ElementsAbstract implements inParag {
    public Strong(List<ElementsAbstract> list) {
        super(list);
    }

    protected String getTag() {
        return "__";
    }

    protected String getOpenTag() {
        return "[b]";
    }

    protected String getCloseTag() {
        return "[/b]";
    }


}
