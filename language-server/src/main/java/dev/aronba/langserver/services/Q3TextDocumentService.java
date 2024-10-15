package dev.aronba.langserver.services;

import dev.aronba.langserver.LanguageServerContext;
import dev.aronba.langserver.diagnostics.DiagnosticsService;
import net.neostralis.q3.compiler.Q3Compiler;
import net.neostralis.q3.compiler.typechecker.Warning;
import net.neostralis.q3.parsers.Line;
import net.neostralis.q3.parsers.exceptions.ParseException;
import org.eclipse.lsp4j.*;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Q3TextDocumentService implements org.eclipse.lsp4j.services.TextDocumentService {


    private final Map<String, String> documentContentMap = new HashMap<>();

    private final LanguageServerContext languageServerContext;
    private final DiagnosticsService diagnosticService;

    public Q3TextDocumentService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
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
    public CompletableFuture<Hover> hover(HoverParams params) {
        Hover hover = new Hover();
        MarkupContent content = new MarkupContent();
        content.setKind("plaintext");
        content.setValue("Simple hover information");
        hover.setContents(content);
        return CompletableFuture.completedFuture(hover);
    }
}