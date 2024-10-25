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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents a workspace with all files and their content
 */
public class BufferedWorkspace {

    private final File rootFolder;
    private final ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BufferedFile> bufferedFiles = new ConcurrentHashMap<>();
    private final LanguageClient languageClient;

    public BufferedWorkspace(File rootFolder, LanguageClient languageClient) {
        this.languageClient = languageClient;
        this.rootFolder = rootFolder;
    }

    /**
     * Opens a file and reads its content
     *
     * @param clientURI the URI of the file
     */
    public void openFileInBuffer(String clientURI) {
        String uri = fixURI(clientURI);
        File file = files.get(uri);

        if (file == null) {
            return;
        }

        BufferedFile bufferedFile = new BufferedFile(file.getAbsolutePath());
        readContentIntoBuffer(bufferedFile);
        bufferedFiles.put(uri, bufferedFile);
    }

    /**
     * Closes a file by removing it from the buffer
     *
     * @param uri the URI of the file
     */
    public void closeFileInBuffer(String uri) {
        bufferedFiles.remove(fixURI(uri));
    }

    /**
     * Changes the content of a file in the buffer
     *
     * @param uri     the URI of the file
     * @param changes the changes to apply
     */
    public void changeFileInBuffer(String uri, List<TextDocumentContentChangeEvent> changes) {
        bufferedFiles.get(fixURI(uri)).updateBufferedContent(changes);
    }

    public BufferedFile getBufferedFile(String uri) {
        return bufferedFiles.get(fixURI(uri));
    }

    /**
     * Indexes the workspace by reading all files and their content
     */
    public void indexWorkspace() {
        try {
            Files.walk(rootFolder.toPath()).filter(path -> path.toString().endsWith(".q3")).forEach(path -> {
                File file = path.toFile();
                files.put(file.toURI().normalize().toString(), file);
            });
        } catch (IOException e) {
            languageClient.logMessage(new MessageParams(MessageType.Error, "Error indexing workspace: " + e));
        }
    }

    private String fixURI(String uri) {
        try {
            URI parsedURI = new URI(uri);
            if (!parsedURI.isAbsolute()) throw new URISyntaxException(uri, "URI is not absolute");
            return parsedURI.normalize().toString().replace("file:///", "file:/");
        } catch (URISyntaxException e) {
            languageClient.logMessage(new MessageParams(MessageType.Error, "Error parsing URI: " + e));
            return "";
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