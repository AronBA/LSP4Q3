package dev.aronba.langserver.services;

import dev.aronba.langserver.buffer.BufferedFile;
import dev.aronba.langserver.utils.LanguageServerContext;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.aronba.langserver.utils.CommonUtils.uriToPath;

public class Q3TextDocumentService implements TextDocumentService {

    private final LanguageServerContext languageServerContext;
    private final DiagnosticsService diagnosticService;

    public Q3TextDocumentService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
        this.diagnosticService = new DiagnosticsService(languageServerContext);
    }


    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        Path uriPath = uriToPath(params.getTextDocument().getUri());
        languageServerContext.getWorkspace().openFileInBuffer(uriPath);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        Path uriPath = uriToPath(params.getTextDocument().getUri());
        languageServerContext.getWorkspace().changeFileInBuffer(uriPath,params.getContentChanges());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        Path uriPath = uriToPath(params.getTextDocument().getUri());
        languageServerContext.getWorkspace().closeFileInBuffer(uriPath);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) { /* TODO document why this method is empty */ }



    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        Path uriPath = uriToPath(params.getTextDocument().getUri());
        BufferedFile file = languageServerContext.getWorkspace().getBufferedFile(uriPath);
        List<Diagnostic> diagnosticReport = diagnosticService.analyze(file);
        return CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnosticReport)));
    }
}