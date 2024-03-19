package markup;

public interface inParag extends BBCode, Markdown {
    void toMarkdown(StringBuilder result);
}
