package markup;

public class Text extends ElementsAbstract implements inParag {
    public Text(String text) {
        super(text);
        //setSEP("");
    }


    @Override
    public void toMarkdown(StringBuilder result) {
        result.setLength(0);
        result.append(text);
        //System.out.println(text + "!" + result);
    }

    @Override
    public void toBBCode(StringBuilder result) {
        result.setLength(0);
        result.append(text);
    }

}
