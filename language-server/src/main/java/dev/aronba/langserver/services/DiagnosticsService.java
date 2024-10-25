package dev.aronba.langserver.services;

import dev.aronba.langserver.buffer.BufferedFile;
import net.neostralis.q3.compiler.Q3Compiler;
import net.neostralis.q3.compiler.typechecker.Warning;
import net.neostralis.q3.parsers.Line;
import net.neostralis.q3.parsers.exceptions.ParseException;
import org.eclipse.lsp4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagnosticsService {

    public List<Diagnostic> analyze(BufferedFile bufferedFile) {
        try {
            String content = bufferedFile.getBufferedContent();
            List<Q3Compiler.Input> inputs = List.of(new Q3Compiler.Input(new Line.OriginFile(bufferedFile), content));
            Q3Compiler.Result result = new Q3Compiler("LSP-COMPILER", inputs, true).run();

            List<Diagnostic> diagnosticList = new ArrayList<>();
            addParseErrors(result, content, bufferedFile, diagnosticList);
            addWarnings(result, content, diagnosticList);

            return diagnosticList;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void addParseErrors(Q3Compiler.Result result, String content, BufferedFile bufferedFile, List<Diagnostic> diagnosticList) {
        if (result.getParseErrors() != null && !result.getParseErrors().isEmpty()) {
            for (ParseException e : result.getParseErrors().getExceptions()) {
                Line line = e.getLine();
                String lineContent = getLineContent(content, line.getLine() - 1);
                Range range = getWordRange(lineContent, line.getLine() - 1, line.getCol());
                diagnosticList.add(new Diagnostic(range, e.getMessage(), DiagnosticSeverity.Error, bufferedFile.getPath()));
            }
        }
    }

    private void addWarnings(Q3Compiler.Result result, String content, List<Diagnostic> diagnosticList) {
        for (Warning warning : result.getWarnings().getWarnings()) {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setSeverity(getSeverity(warning.getSeverity()));
            diagnostic.setMessage(warning.getMessage());

            String lineContent = getLineContent(content, warning.getLine().getLine());
            Range range = getWordRange(lineContent, warning.getLine().getLine() - 1, warning.getLine().getCol() - 1);
            diagnostic.setRange(range);
            diagnosticList.add(diagnostic);
        }
    }

    private DiagnosticSeverity getSeverity(Warning.Severity severity) {
        return switch (severity) {
            case ERROR -> DiagnosticSeverity.Error;
            case WARN -> DiagnosticSeverity.Warning;
            case INFO -> DiagnosticSeverity.Information;
        };
    }

    private String getLineContent(String content, int lineNumber) {
        String[] lines = content.split("\n");
        return lineNumber < lines.length ? lines[lineNumber] : "";
    }

    private Range getWordRange(String lineContent, int lineNumber, int charPosition) {
        int start = charPosition;
        int end = charPosition;

        while (start > 0 && Character.isLetterOrDigit(lineContent.charAt(start - 1))) {
            start--;
        }
        while (end < lineContent.length() && Character.isLetterOrDigit(lineContent.charAt(end))) {
            end++;
        }
        return new Range(new Position(lineNumber, start), new Position(lineNumber, end));
    }
}