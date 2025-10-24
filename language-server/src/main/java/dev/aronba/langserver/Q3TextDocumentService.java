package dev.aronba.langserver;

import dev.aronba.lsp4q3.parser.*;
import dev.aronba.lsp4q3.parser.Diagnostic;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class Q3TextDocumentService implements TextDocumentService {

    private final LanguageServerContext languageServerContext;
    private final Map<String, ParsedDocument> openDocuments = new ConcurrentHashMap<>();

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        ParsedDocument parsedDocument = Q3ParserFacade.parseFile(uri, text);
        openDocuments.put(uri, parsedDocument);
    }


    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();

        if (!params.getContentChanges().isEmpty()) {
            ParsedDocument parsedDocument = openDocuments.get(uri);
            String text = params.getContentChanges().get(0).getText();
            parsedDocument.setText(text);
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        openDocuments.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        String uri = params.getTextDocument().getUri();
        String code = openDocuments.get(uri).getText();

        List<org.eclipse.lsp4j.Diagnostic> diagnostics = computeDiagnostics(code);

        RelatedFullDocumentDiagnosticReport full;
        if (diagnostics.isEmpty()) {
            full = new RelatedFullDocumentDiagnosticReport(Collections.emptyList());
        } else {
            full = new RelatedFullDocumentDiagnosticReport(diagnostics);
        }
        return CompletableFuture.completedFuture(new DocumentDiagnosticReport(full));

    }


    private List<org.eclipse.lsp4j.Diagnostic> computeDiagnostics(String code) {
        ParseResult result = Q3ParserFacade.parseBlock(code);
        List<Diagnostic> parserDiagnostics = result.getDiagnostics();
        return mapDiagnostics(parserDiagnostics);
    }


    private List<org.eclipse.lsp4j.Diagnostic> mapDiagnostics(List<Diagnostic> parserDiagnostics) {

        List<org.eclipse.lsp4j.Diagnostic> lspDiagnostics = new ArrayList<>();
        for (Diagnostic pd : parserDiagnostics) {
            org.eclipse.lsp4j.Diagnostic d = new org.eclipse.lsp4j.Diagnostic();
            d.setMessage(pd.getMessage());
            d.setSeverity(org.eclipse.lsp4j.DiagnosticSeverity.Error);
            d.setRange(new org.eclipse.lsp4j.Range(
                    new org.eclipse.lsp4j.Position(pd.getLine() - 1, pd.getCharPosition()),
                    new org.eclipse.lsp4j.Position(pd.getLine() - 1, pd.getCharPosition() + 1)
            ));
            lspDiagnostics.add(d);
        }
        return lspDiagnostics;
    }


    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {

        String uri = params.getTextDocument().getUri();
        ParsedDocument doc = openDocuments.get(uri);

        if (doc == null || doc.getSymbolTable() == null) {
            return CompletableFuture.completedFuture(null);
        }
        Position pos = params.getPosition();
        String word = getWordAtPosition(doc.getText(), pos);

        Symbol symbol = doc.getSymbolTable().resolve(word);
        if (symbol == null) {
            this.languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info,"symbol is null"));
            return CompletableFuture.completedFuture(null);
        }

        this.languageServerContext.getClient().showMessage(new MessageParams(MessageType.Info,"Hover completed"));

        MarkupContent content = new MarkupContent();
        content.setKind("markdown");
        content.setValue("**" + symbol.getName() + "**: `" + symbol.getType() + "`");


        Hover hover = new Hover();
        hover.setContents(content);
        return CompletableFuture.completedFuture(hover);


    }

    private String getWordAtPosition(String text, Position pos) {
        String[] lines = text.split("\n");
        if (pos.getLine() < 0 || pos.getLine() >= lines.length) return null;
        String line = lines[pos.getLine()];

        int charIndex = Math.min(pos.getCharacter(), line.length());
        if (charIndex < 0) return null;

        int start = charIndex;
        while (start > 0 && Character.isJavaIdentifierPart(line.charAt(start - 1))) {
            start--;
        }

        int end = charIndex;
        while (end < line.length() && Character.isJavaIdentifierPart(line.charAt(end))) {
            end++;
        }

        return line.substring(start, end);
    }
}

