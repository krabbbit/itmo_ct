package markup;

import java.util.List;

public class OrderedList implements PLInterface {
    private static final String OPEN = "[list=1]";
    private static final String CLOSE = "[/list]";
    private final List<ListItem> list;

    public OrderedList(List<ListItem> list) {
        this.list = list;
    }


    @Override
    public void toBBCode(StringBuilder result) {
        result.setLength(0);
        // result.append(SEP);
        StringBuilder k = new StringBuilder();
        for (ListItem elem : this.list) {
            elem.toBBCode(k);
            result.append(k);
        }
        result.append(CLOSE);
        result.insert(0, OPEN);
    }
}
