package dev.aronba.lsp4q3.parser;

import lombok.Builder;
import lombok.Data;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

@Data
@Builder
public class ParseResult {
    private final ParseTree tree;
    private final List<Diagnostic> diagnostics;
}
