package dev.aronba.langserver;

import dev.aronba.langserver.services.Q3NotebookDocumentService;
import dev.aronba.langserver.services.Q3TextDocumentService;
import dev.aronba.langserver.services.Q3WorkspaceService;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Q3LanguageServer implements LanguageServer, LanguageClientAware {


    private final Q3TextDocumentService q3TextDocumentService;
    private final Q3WorkspaceService q3WorkspaceService;
    private final Q3NotebookDocumentService q3NotebookDocumentService;

    private ClientCapabilities clientCapabilities;
    private final LanguageServerContext languageServerContext;

    private int errorCode = 1;

    public Q3LanguageServer() {
        languageServerContext = LanguageServerContext.builder().build();

        q3TextDocumentService = new Q3TextDocumentService(languageServerContext);
        q3WorkspaceService = new Q3WorkspaceService(languageServerContext);
        q3NotebookDocumentService = new Q3NotebookDocumentService(languageServerContext);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info, "LSP Initializing"));

        ServerCapabilities capabilities = getServerCapabilities();

        InitializeResult result = new InitializeResult(capabilities);
        this.clientCapabilities = initializeParams.getCapabilities();

        return CompletableFuture.supplyAsync(() -> result);
    }

    private ServerCapabilities getServerCapabilities() {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
//        capabilities.setCompletionProvider(new CompletionOptions());
        capabilities.setHoverProvider(true);
//        capabilities.setDefinitionProvider(true);
//        capabilities.setReferencesProvider(true);
//        capabilities.setDocumentSymbolProvider(true);
//        capabilities.setWorkspaceSymbolProvider(true);
//        capabilities.setCodeActionProvider(new CodeActionOptions());
//        capabilities.setDocumentFormattingProvider(true);
//        capabilities.setDocumentRangeFormattingProvider(true);
//        capabilities.setRenameProvider(true);
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
        System.exit(errorCode);
    }

    @Override
    public Q3NotebookDocumentService getNotebookDocumentService() {
        return q3NotebookDocumentService;
    }
    @Override
    public org.eclipse.lsp4j.services.TextDocumentService getTextDocumentService() {
        return this.q3TextDocumentService;
    }

    @Override
    public org.eclipse.lsp4j.services.WorkspaceService getWorkspaceService() {
        return this.q3WorkspaceService;
    }

    @Override
    public void connect(LanguageClient languageClient) {
        this.languageServerContext.setClient(languageClient);
    }
}
