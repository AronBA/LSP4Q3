package dev.aronba.langserver.formatting;

import dev.aronba.langserver.LanguageServerContext;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;

import java.util.ArrayList;
import java.util.List;



//TODO make formatting service better and not this trash
public class FormattingService {
    private final LanguageServerContext languageServerContext;
    private static final String INDENTATION = "  ";

    public FormattingService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
    }

    public List<? extends TextEdit> format(String content) {
        List<TextEdit> edits = new ArrayList<>();
        String[] lines = content.split("\n");
        int indentationLevel = 0;

        for (int i = 0; i < lines.length; i++) {
            String originalLine = lines[i];
            String formattedLine = applyFormattingRules(originalLine, indentationLevel);

            if (originalLine.contains("}")) {
                indentationLevel--;
            }

            if (!originalLine.equals(formattedLine)) {
                Range range = new Range(new Position(i, 0), new Position(i, originalLine.length()));
                TextEdit edit = new TextEdit(range, formattedLine);
                edits.add(edit);
            }

            if (originalLine.contains("{")) {
                indentationLevel++;
            }
        }

        return edits;
    }

    private String applyFormattingRules(String line, int indentationLevel) {
        line = line.replaceAll("\\s+$", "");
        line = line.replaceAll("\\s*([+\\-*/=<>!&|^%])\\s*", " $1 ");
        line = line.replaceAll("\\b(return|throw|want)\\s+", "$1 ");
        line = line.replaceAll(";\\s*", "; ");
        line = line.replaceAll("\\s*#\\s*", " # ");
        line = line.replaceAll("\\s*->\\s*", " -> ");
        line = line.replaceAll("\\)\\s*\\{", ") {");
        line = line.replaceAll("\\s*:\\s*", " : ");
        line = line.replaceAll("\\s*\\?\\s*", " ? ");
        line = line.replaceAll("\\b(class|enum)\\s*!\\s*", "$1! ");
        line = line.replaceAll("\\b(enum)\\s*\\{", "$1 {");

        line = applyIndentation(line, indentationLevel);
        return line;
    }

    private String applyIndentation(String line, int indentationLevel) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return line;
        }
        return INDENTATION.repeat(indentationLevel) + trimmedLine;
    }
}