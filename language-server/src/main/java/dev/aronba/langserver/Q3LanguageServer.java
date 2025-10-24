package dev.aronba.langserver;

import lombok.Getter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Q3LanguageServer implements LanguageServer, LanguageClientAware {

    private final Q3TextDocumentService q3TextDocumentService;
    private final LanguageServerContext languageServerContext;
    private ClientCapabilities clientCapabilities;

    @Getter
    private int errorCode = 1;

    public Q3LanguageServer() {
        this.languageServerContext = LanguageServerContext.builder().build();
        this.q3TextDocumentService = new Q3TextDocumentService(languageServerContext);
    }

    @Override
    public void setTrace(SetTraceParams params) {
        // intentionally left empty (could be used for enabling verbose tracing)
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        this.clientCapabilities = initializeParams.getCapabilities();

        ServerCapabilities capabilities = getServerCapabilities();
        InitializeResult result = new InitializeResult(capabilities);

        return CompletableFuture.completedFuture(result);
    }

    private ServerCapabilities getServerCapabilities() {
        ServerCapabilities capabilities = new ServerCapabilities();

        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions());
        capabilities.setHoverProvider(new HoverOptions());

        return capabilities;
    }

    @Override
    public void initialized(InitializedParams params) {
        LanguageServer.super.initialized(params);

        if (isDynamicCompletionRegistrationSupported()) {
            CompletionOptions completionOptions = new CompletionOptions();
            completionOptions.setResolveProvider(true);

            Registration registration =
                    new Registration(UUID.randomUUID().toString(), "textDocument/completion", completionOptions);

            RegistrationParams regParams = new RegistrationParams(Collections.singletonList(registration));

            languageServerContext.getClient()
                    .registerCapability(regParams)
                    .whenComplete((v, ex) -> {
                        if (ex != null) {
                            languageServerContext.getClient().showMessage(
                                    new MessageParams(MessageType.Warning, "Dynamic registration for completion failed: " + ex.getMessage())
                            );
                        }
                    });
        }

        if (languageServerContext.getClient() != null) {
            languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info, "Q3 Language Server initialized"));
        }
    }

    /**
     * Returns true if the client explicitly indicates support for dynamic completion registration.
     */
    private boolean isDynamicCompletionRegistrationSupported() {
        if (this.clientCapabilities == null) return false;
        TextDocumentClientCapabilities textDocCaps = this.clientCapabilities.getTextDocument();
        if (textDocCaps == null) return false;
        CompletionCapabilities completionCaps = textDocCaps.getCompletion();
        if (completionCaps == null) return false;

        // client says whether it supports dynamic registration (true => supports dynamic registration)
        Boolean dynamic = completionCaps.getDynamicRegistration();
        return Boolean.TRUE.equals(dynamic);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        errorCode = 0;
        return CompletableFuture.completedFuture(new Object());
    }

    @Override
    public void exit() {
        if (languageServerContext.getClient() != null) {
            if (errorCode != 0) {
                languageServerContext.getClient().showMessage(new MessageParams(MessageType.Error,
                        "Q3 Language Server exited with error code " + errorCode));
            } else {
                languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info,
                        "Q3 Language Server exited successfully"));
            }
        }
        System.exit(errorCode);
    }

    @Override
    public NotebookDocumentService getNotebookDocumentService() {
        return null;
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.q3TextDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }

    /**
     * Called by the launcher to attach the client to the server.
     */
    @Override
    public void connect(LanguageClient languageClient) {
        this.languageServerContext.setClient(languageClient);
    }
}
