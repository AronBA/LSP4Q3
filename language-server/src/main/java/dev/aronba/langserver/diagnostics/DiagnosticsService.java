package dev.aronba.langserver.diagnostics;

import dev.aronba.langserver.LanguageServerContext;
import net.neostralis.q3.compiler.Q3Compiler;
import net.neostralis.q3.compiler.typechecker.Warning;
import net.neostralis.q3.parsers.Line;
import net.neostralis.q3.parsers.exceptions.ParseException;
import org.eclipse.lsp4j.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticsService {

    private final LanguageServerContext languageServerContext;
    public DiagnosticsService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
    }

    public List<Diagnostic> analyze(String content, String uri) {

        List<Q3Compiler.Input> inputs = List.of(new Q3Compiler.Input(new Line.OriginFile(new File(URI.create(uri))), content));
        Q3Compiler.Result result = new Q3Compiler("LSP-COMPILER", inputs, true).run();

        List<Diagnostic> diagnosticList = new ArrayList<>();

        if (result.getParseErrors() != null && !result.getParseErrors().isEmpty()) {
            for (ParseException e : result.getParseErrors().getExceptions()) {
                Line line = e.getLine();
                String lineContent = getLineContent(content, line.getLine() - 1);
                Range range = getWordRange(lineContent, line.getLine() - 1, line.getCol());
                Diagnostic diagnostic = new Diagnostic(range, e.getMessage(), DiagnosticSeverity.Error, uri, lineContent);
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

            String lineContent = getLineContent(content, warning.getLine().getLine());
            Range range = getWordRange(lineContent, warning.getLine().getLine() - 1, warning.getLine().getCol() - 1);
            diagnostic.setRange(range);
            diagnosticList.add(diagnostic);
        }

        return diagnosticList;
    }

    private String getLineContent(String content, int lineNumber) {
        String[] lines = content.split("\n");
        return lineNumber < lines.length ? lines[lineNumber] : "";
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
}
