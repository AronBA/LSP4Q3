package dev.aronba.langserver;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.concurrent.CompletableFuture;

public class Q3LanguageServer implements LanguageServer, LanguageClientAware {


    private Q3TextDocumentService textDocumentService;
    private Q3WorkspaceService workspaceService;
    private LanguageClient client;

    private int errorCode = 1;


    public Q3LanguageServer() {
        textDocumentService = new Q3TextDocumentService();
        workspaceService = new Q3WorkspaceService();
    }




    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {

        final InitializeResult result = new InitializeResult(new ServerCapabilities());
        result.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        CompletionOptions completionOptions = new CompletionOptions();
        result.getCapabilities().setCompletionProvider(completionOptions);

        return CompletableFuture.supplyAsync(() -> result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        errorCode = 0;
        return null;
    }

    @Override
    public void exit() {
        System.exit(errorCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    @Override
    public void connect(LanguageClient languageClient) {
        this.client = languageClient;
    }
}
