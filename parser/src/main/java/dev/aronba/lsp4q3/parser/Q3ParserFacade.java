package dev.aronba.lsp4q3.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Q3ParserFacade {

    public static ParseResult parseBlock(String code) {
        return parse(code);
    }

    public static ParsedDocument parseFile(String uri, String text) {
        ParseResult parseResult = parse(text);

        Scope symbolTable = null;
        if (parseResult.getTree() != null) {
            SymbolTableBuilder builder = new SymbolTableBuilder();
            builder.visit(parseResult.getTree());
            symbolTable = builder.getGlobalScope();
        }

        return ParsedDocument.builder()
                .uri(uri)
                .text(text)
                .diagnostics(parseResult.getDiagnostics())
                .parseTree(parseResult.getTree())
                .symbolTable(symbolTable)
                .build();
    }

    private static ParseResult parse(String text) {
        CharStream input = CharStreams.fromString(text);
        Q3Lexer lexer = new Q3Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Q3Parser parser = new Q3Parser(tokens);

        parser.removeErrorListeners();

        CollectingErrorListener listener = new CollectingErrorListener();
        parser.addErrorListener(listener);

        ParseTree tree = parser.program();
        return ParseResult.builder()
                .tree(tree)
                .diagnostics(listener.getDiagnostics())
                .build();
    }
}
