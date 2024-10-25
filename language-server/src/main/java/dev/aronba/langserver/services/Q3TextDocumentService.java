package dev.aronba.langserver.services;

import dev.aronba.langserver.buffer.BufferedFile;
import dev.aronba.langserver.utils.LanguageServerContext;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Q3TextDocumentService implements TextDocumentService {

    private final LanguageServerContext languageServerContext;
    private final DiagnosticsService diagnosticService;

    public Q3TextDocumentService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
        this.diagnosticService = new DiagnosticsService();
    }


    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        languageServerContext.getWorkspace().openFileInBuffer(params.getTextDocument().getUri());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        languageServerContext.getWorkspace().changeFileInBuffer(params.getTextDocument().getUri(), params.getContentChanges());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        languageServerContext.getWorkspace().closeFileInBuffer(params.getTextDocument().getUri());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) { /* TODO document why this method is empty */ }



    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        String uri = params.getTextDocument().getUri();
        BufferedFile file = languageServerContext.getWorkspace().getBufferedFile(uri);
        List<Diagnostic> diagnosticReport = diagnosticService.analyze(file);
        return CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnosticReport)));
    }
}