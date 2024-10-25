package dev.aronba.langserver.buffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;

import java.io.File;
import java.util.List;

/**
 * Represents a file with its content in memory
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class BufferedFile extends File {

    /**
     * -- GETTER --
     *  Reads the content of the file into the buffer
     */
    private List<String> bufferedLines;

    public BufferedFile(String pathname) {
        super(pathname);
    }

    /**
     * Updates the content of the file in the buffer
     *
     * @param changes the changes to apply
     */
    public void updateBufferedContent(List<TextDocumentContentChangeEvent> changes) {
        String content = changes.getFirst().getText();
        this.bufferedLines = List.of(content.split("\n"));
    }

    /**
     * Reads the content of the file into the buffer
     */
    public String getBufferedLine(Position position) {
        return bufferedLines.get(position.getLine());
    }

    /**
     * Reads the content of the file into the buffer
     */
    public char getBufferedChar(Position position) {
        return getBufferedLine(position).charAt(position.getCharacter());
    }

    public String getBufferedContent() {
        return String.join("\n", bufferedLines);
    }

    /**
     * Reads the content of the file into the buffer
     */
    public String getBufferedWord(Position position) {
        String line = getBufferedLine(position);
        int start = position.getCharacter();
        int end = position.getCharacter();
        while (start > 0 && Character.isLetterOrDigit(line.charAt(start - 1))) {
            start--;
        }
        while (end < line.length() && Character.isLetterOrDigit(line.charAt(end))) {
            end++;
        }
        return line.substring(start, end);
    }

}
