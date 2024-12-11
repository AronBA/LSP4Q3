package dev.aronba.langserver.buffer;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents a workspace with all files and their content
 */
public class BufferedWorkspace {

    private final File rootFolder;
    private final ConcurrentHashMap<Path, File> files = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Path, BufferedFile> bufferedFiles = new ConcurrentHashMap<>();
    private final LanguageClient languageClient;

    public BufferedWorkspace(File rootFolder, LanguageClient languageClient) {
        this.languageClient = languageClient;
        this.rootFolder = rootFolder;
    }

    /**
     * Opens a file and reads its content
     *
     * @param path the Path of the file
     */
    public void openFileInBuffer(Path path) {
        File file = files.get(path);

        if (file == null) {
            return;
        }

        BufferedFile bufferedFile = new BufferedFile(file.getAbsolutePath());
        readContentIntoBuffer(bufferedFile);
        bufferedFiles.put(path, bufferedFile);
    }

    /**
     * Closes a file by removing it from the buffer
     *
     * @param path the URI of the file
     */
    public void closeFileInBuffer(Path path) {
        bufferedFiles.remove(path);
    }

    /**
     * Changes the content of a file in the buffer
     *
     * @param path     the URI of the file
     * @param changes the changes to apply
     */
    public void changeFileInBuffer(Path path, List<TextDocumentContentChangeEvent> changes) {
        bufferedFiles.get(path).updateBufferedContent(changes);
    }

    public BufferedFile getBufferedFile(Path path) {
        return bufferedFiles.get(path);
    }

    /**
     * Indexes the workspace by reading all files and their content
     */
    public void indexWorkspace() {
        try {
            Files.walk(rootFolder.toPath()).filter(path -> path.toString().endsWith(".q3")).forEach(path -> files.put(path, path.toFile()));
        } catch (IOException e) {
            languageClient.logMessage(new MessageParams(MessageType.Error, "Error indexing workspace: " + e));
        }
    }
    private void readContentIntoBuffer(BufferedFile bufferedFile) {
        try {
            List<String> lines = Files.readAllLines(bufferedFile.toPath());
            bufferedFile.setBufferedLines(lines);
        } catch (IOException e) {
            languageClient.logMessage(new MessageParams(MessageType.Error, "Error reading file content: " + e));
        }
    }
}