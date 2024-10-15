package dev.aronba.langserver;

import lombok.Setter;
import net.neostralis.q3.compiler.Q3Compiler;
import net.neostralis.q3.compiler.typechecker.Warning;
import net.neostralis.q3.parsers.Line;
import net.neostralis.q3.parsers.exceptions.ParseException;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Q3TextDocumentService implements TextDocumentService {


    @Setter
    private LanguageClient client;

    CodeAnalysesProvider codeAnalysesProvider = new CodeAnalysesProvider();


    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        String uri = params.getTextDocument().getUri();

        Q3Compiler.Result result = null;
        File file = null;
        try {
            file = new File(new URI(uri));
            result = codeAnalysesProvider.run(file);
        } catch (URISyntaxException e) {
            return null;
        }

        List<Diagnostic> diagnosticList = new ArrayList<>();

        if (result.getParseErrors() != null && !result.getParseErrors().isEmpty()) {
            for (ParseException e : result.getParseErrors().getExceptions()) {
                Line line = e.getLine();
                String lineContent = getLineContent(file, line.getLine() - 1);
                Range range = getWordRange(lineContent, line.getLine() -1 , line.getCol());
                Diagnostic diagnostic = new Diagnostic(range, e.getMessage(), DiagnosticSeverity.Error, file.getPath(),lineContent);
                diagnosticList.add(diagnostic);
            }
        }

        for (Warning warning : result.getWarnings().getWarnings()) {
            Diagnostic diagnostic = new Diagnostic();

            switch (warning.getSeverity()) {
                case ERROR:
                    diagnostic.setSeverity(DiagnosticSeverity.Error);
                    break;
                case WARN:
                    diagnostic.setSeverity(DiagnosticSeverity.Warning);
                    break;
                case INFO:
                    diagnostic.setSeverity(DiagnosticSeverity.Information);
                    break;
            }
            diagnostic.setMessage(warning.getMessage());


            String lineContent = getLineContent(file, warning.getLine().getLine());
            Range range = getWordRange(lineContent,warning.getLine().getLine() - 1, warning.getLine().getCol() - 1);
            diagnostic.setRange(range);
            diagnosticList.add(diagnostic);
        }

        client.logMessage(new MessageParams(MessageType.Info, diagnosticList.toString()));
        client.showMessage(new MessageParams(MessageType.Info, "Document Diagnosed"));
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, Collections.emptyList()));
        return CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnosticList)));
    }

    private String getLineContent(File file, int lineNumber) {
        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines.skip(lineNumber).findFirst().orElse("");
        } catch (IOException e) {
            return "";
        }
    }

    private Range getWordRange(String lineContent, int lineNumber, int charPosition) {
        int start = charPosition;
        int end = charPosition;

        while (start > 0 && start - 1 < lineContent.length() && Character.isLetterOrDigit(lineContent.charAt(start - 1))) {
            start--;
        }
        while (end < lineContent.length() && Character.isLetterOrDigit(lineContent.charAt(end))) {
            end++;
        }

        return new Range(new Position(lineNumber, start), new Position(lineNumber, end));
    }


    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        if (client != null) client.showMessage(new MessageParams(MessageType.Info, "Document Opened"));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        diagnostic(new DocumentDiagnosticParams(didChangeTextDocumentParams.getTextDocument()));
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

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
    }

    @Override
    public void didSave(DidSaveTextDocumentParams didSaveTextDocumentParams) {
    }
}
