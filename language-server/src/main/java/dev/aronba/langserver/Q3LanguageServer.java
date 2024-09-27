package dev.aronba.langserver;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.*;

import java.util.concurrent.CompletableFuture;

public class Q3LanguageServer implements LanguageServer, LanguageClientAware {
    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        return null;
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return null;
    }

    @Override
    public void exit() {

    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return null;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }

    @Override
    public void connect(LanguageClient languageClient) {

    }
}
