package dev.aronba.lsp4q3.parser;

import lombok.Builder;
import lombok.Data;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

@Data
@Builder
public class ParsedDocument {
    private String uri;
    private String text;
    private ParseTree parseTree;
    private List<Diagnostic> diagnostics;
    private Scope symbolTable;
}
