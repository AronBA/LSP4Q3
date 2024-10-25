package buffer;

import dev.aronba.langserver.buffer.BufferedFile;
import dev.aronba.langserver.buffer.BufferedWorkspace;
import org.eclipse.lsp4j.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BufferedWorkspaceTest {

    private BufferedWorkspace bufferedWorkspace;
    private LanguageClient languageClient;
    private File rootFolder;

    @BeforeEach
    void setUp() {
        languageClient = mock(LanguageClient.class);
        rootFolder = new File("testRoot");
        bufferedWorkspace = new BufferedWorkspace(rootFolder, languageClient);
    }


    @Test
    void openFileInBuffer_invalidURI() {
        bufferedWorkspace.openFileInBuffer("invalidURI");
        assertNull(bufferedWorkspace.getBufferedFile("invalidURI"));
    }

    @Test
    void closeFileInBuffer_existingFile() {
        File file = new File(rootFolder, "test.q3");
        bufferedWorkspace.indexWorkspace();
        bufferedWorkspace.openFileInBuffer(file.toURI().toString());

        bufferedWorkspace.closeFileInBuffer(file.toURI().toString());
        assertNull(bufferedWorkspace.getBufferedFile(file.toURI().toString()));
    }


    @Test
    void changeFileInBuffer_invalidURI() {
        TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent();
        changeEvent.setText("new content");

        assertThrows(NullPointerException.class, () ->
                bufferedWorkspace.changeFileInBuffer("invalidURI", List.of(changeEvent))
        );
    }




}