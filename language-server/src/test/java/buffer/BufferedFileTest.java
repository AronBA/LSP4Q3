package buffer;

import dev.aronba.langserver.buffer.BufferedFile;
import org.junit.jupiter.api.BeforeEach;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class BufferedFileTest {

    private BufferedFile bufferedFile;

    @BeforeEach
    void setUp() {
        bufferedFile = new BufferedFile("test.txt");
    }

    @Test
    void updateBufferedContent_singleLine() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("This is a test line.");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertEquals("This is a test line.", bufferedFile.getBufferedLine(new Position(0, 0)));
    }

    @Test
    void updateBufferedContent_multipleLines() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("Line 1\nLine 2\nLine 3");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertEquals("Line 1", bufferedFile.getBufferedLine(new Position(0, 0)));
        assertEquals("Line 2", bufferedFile.getBufferedLine(new Position(1, 0)));
        assertEquals("Line 3", bufferedFile.getBufferedLine(new Position(2, 0)));
    }

    @Test
    void getBufferedChar_validPosition() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("Hello");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertEquals('e', bufferedFile.getBufferedChar(new Position(0, 1)));
    }

    @Test
    void getBufferedChar_invalidPosition() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("Hello");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedFile.getBufferedChar(new Position(0, 10)));
    }

    @Test
    void getBufferedWord_singleWord() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("Hello");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertEquals("Hello", bufferedFile.getBufferedWord(new Position(0, 1)));
    }

    @Test
    void getBufferedWord_multipleWords() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("Hello world");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertEquals("Hello", bufferedFile.getBufferedWord(new Position(0, 1)));
        assertEquals("world", bufferedFile.getBufferedWord(new Position(0, 7)));
    }

    @Test
    void getBufferedWord_edgeCases() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("Hello world");
        bufferedFile.updateBufferedContent(List.of(changeEvent));
        assertEquals("Hello", bufferedFile.getBufferedWord(new Position(0, 0)));
        assertEquals("world", bufferedFile.getBufferedWord(new Position(0, 11)));
    }
}