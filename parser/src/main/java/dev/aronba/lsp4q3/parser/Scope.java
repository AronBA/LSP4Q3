package dev.aronba.lsp4q3.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Getter
public class Scope {
    private final Scope parent;
    private final Map<String, Symbol> symbols = new HashMap<>();


    public void define(Symbol symbol) {
        this.symbols.put(symbol.getName(), symbol);
    }

    public Symbol resolve(String name) {
        Symbol symbol = this.symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        if (parent != null) {
            return parent.resolve(name);
        }
        return null;
    }
}
