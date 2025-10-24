package dev.aronba.lsp4q3.parser;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Diagnostic{
        int line;
        int charPosition;
        String message;
}
