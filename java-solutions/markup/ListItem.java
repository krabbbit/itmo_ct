package markup;

import java.util.List;

public class ListItem {
    private static final String OPEN = "[*]";
    private final List<PLInterface> list;

    public ListItem(List<PLInterface> list) {
        this.list = list;
    }

    // @Override
    public void toBBCode(StringBuilder result) {
        result.setLength(0);
        // result.append(SEP);
        StringBuilder k = new StringBuilder();
        for (PLInterface elem : this.list) {
            elem.toBBCode(k);
            result.append(k);
        }
        result.insert(0, OPEN);
    }
}
