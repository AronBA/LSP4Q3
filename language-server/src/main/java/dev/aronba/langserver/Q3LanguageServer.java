package dev.aronba.langserver;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Q3LanguageServer implements LanguageServer, LanguageClientAware {


    private Q3TextDocumentService textDocumentService;
    private Q3WorkspaceService workspaceService;
    private LanguageClient client;
    private ClientCapabilities clientCababilities;

    private int errorCode = 1;

    public Q3LanguageServer() {
        textDocumentService = new Q3TextDocumentService();
        workspaceService = new Q3WorkspaceService();
    }


    @Override
    public void setTrace(SetTraceParams params) {
        return;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        client.showMessage(new MessageParams(MessageType.Info, "LSP Initializing"));

        ServerCapabilities capabilities = getServerCapabilities();

        InitializeResult result = new InitializeResult(capabilities);
        this.clientCababilities = initializeParams.getCapabilities();

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
            client.registerCapability(new RegistrationParams(List.of(registration)));
        }
    }

    private boolean isDynamicCompletionRegistrationSupported() {
        TextDocumentClientCapabilities textDocumentClientCapabilities = this.clientCababilities.getTextDocument();
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
        this.textDocumentService.setClient(languageClient);
        client.showMessage(new MessageParams(MessageType.Info,"Connected"));
    }
}
