package dev.aronba.langserver.services;

import dev.aronba.langserver.LanguageServerContext;
import dev.aronba.langserver.diagnostics.DiagnosticsService;
import dev.aronba.langserver.formatting.FormattingService;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Q3TextDocumentService implements org.eclipse.lsp4j.services.TextDocumentService {


    //map uri to content
    private final Map<String, String> documentContentMap = new HashMap<>();

    private final LanguageServerContext languageServerContext;
    private final DiagnosticsService diagnosticService;
    private final FormattingService formattingService;

    public Q3TextDocumentService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
        this.formattingService = new FormattingService(languageServerContext);
        this.diagnosticService = new DiagnosticsService(languageServerContext);
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        String uri = params.getTextDocument().getUri();
        String content = documentContentMap.get(uri);

        if (content == null) {
            return CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(Collections.emptyList())));
        }

        List<Diagnostic> diagnosticList = diagnosticService.analyze(documentContentMap.get(params.getTextDocument().getUri()),uri);
        return CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnosticList)));
    }
    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String content = params.getTextDocument().getText();
        documentContentMap.put(uri, content);

        diagnostic(new DocumentDiagnosticParams(new TextDocumentIdentifier(params.getTextDocument().getUri())));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String content = params.getContentChanges().get(0).getText();
        documentContentMap.put(uri, content);
        diagnostic(new DocumentDiagnosticParams(params.getTextDocument()));
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documentContentMap.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        diagnostic(new DocumentDiagnosticParams(params.getTextDocument()));
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {

        List<? extends TextEdit> textEdits = formattingService.format(documentContentMap.get(params.getTextDocument().getUri()));
        return CompletableFuture.completedFuture(textEdits);
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Hover hover = new Hover();
        MarkupContent content = new MarkupContent();
        content.setKind("plaintext");
        content.setValue("Simple hover information");
        hover.setContents(content);
        return CompletableFuture.completedFuture(hover);
    }
}