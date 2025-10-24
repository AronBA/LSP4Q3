package dev.aronba.lsp4q3.parser;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Symbol {
    private final String name;
    private final String type;
    private final int line;
    private final int column;
}
