package dev.aronba.langserver;

import dev.aronba.langserver.services.Q3TextDocumentService;

import dev.aronba.langserver.buffer.BufferedWorkspace;
import dev.aronba.langserver.utils.LanguageServerContext;
import lombok.Getter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Q3LanguageServer implements LanguageServer, LanguageClientAware {


    private final Q3TextDocumentService q3TextDocumentService;

    private ClientCapabilities clientCapabilities;
    private final LanguageServerContext languageServerContext;

    @Getter
    private int errorCode = 1;

    public Q3LanguageServer() {
        languageServerContext = LanguageServerContext.builder().build();
        q3TextDocumentService = new Q3TextDocumentService(languageServerContext);
    }

    @Override
    public void setTrace(SetTraceParams params) { /* document why this method is empty */ }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {

        ServerCapabilities capabilities = getServerCapabilities();
        InitializeResult result = new InitializeResult(capabilities);
        this.clientCapabilities = initializeParams.getCapabilities();

        String uriString = initializeParams.getWorkspaceFolders().getFirst().getUri();
        URI uri = null;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File file = new File(uri);

        BufferedWorkspace bufferedWorkspace = new BufferedWorkspace(file, languageServerContext.getClient());
        bufferedWorkspace.indexWorkspace();
        languageServerContext.setWorkspace(bufferedWorkspace);

        return CompletableFuture.completedFuture(result);
    }

    private ServerCapabilities getServerCapabilities() {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions());
        return capabilities;
    }


    @Override
    public void initialized(InitializedParams params) {
        LanguageServer.super.initialized(params);
        if (isDynamicCompletionRegistrationSupported()) {
            CompletionOptions completionOptions = new CompletionOptions();
            completionOptions.setResolveProvider(true);
            Registration registration = new Registration(UUID.randomUUID().toString(),"textDocument/completion", completionOptions);
            languageServerContext.getClient().registerCapability(new RegistrationParams(List.of(registration)));
        }
        languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info, "Q3 Language Server initialized"));
    }

    private boolean isDynamicCompletionRegistrationSupported() {
        TextDocumentClientCapabilities textDocumentClientCapabilities = this.clientCapabilities.getTextDocument();
        return textDocumentClientCapabilities != null && textDocumentClientCapabilities.getCompletion() != null
                && Boolean.FALSE.equals(textDocumentClientCapabilities.getCompletion().getDynamicRegistration());
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        errorCode = 0;
        return CompletableFuture.completedFuture(new Object());
    }

    @Override
    public void exit() {
        if (errorCode != 0) {
            languageServerContext.getClient().showMessage(new MessageParams(MessageType.Error, "Q3 Language Server exited with error code " + errorCode));
        } else {
            languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info, "Q3 Language Server exited successfully"));
        }
        System.exit(errorCode);
    }

    @Override
    public NotebookDocumentService getNotebookDocumentService() {
        return null;
    }
    @Override
    public org.eclipse.lsp4j.services.TextDocumentService getTextDocumentService() {
        return this.q3TextDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }

    @Override
    public void connect(LanguageClient languageClient) {
        this.languageServerContext.setClient(languageClient);
    }
}
