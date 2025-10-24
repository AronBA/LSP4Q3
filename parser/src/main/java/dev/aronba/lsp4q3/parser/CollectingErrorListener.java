package dev.aronba.lsp4q3.parser;

import lombok.Getter;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

@Getter
class CollectingErrorListener extends BaseErrorListener {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        diagnostics.add(new Diagnostic(line, charPositionInLine, msg));
    }

}
