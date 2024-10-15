package dev.aronba.langserver;

import lombok.Builder;
import lombok.Data;
import org.eclipse.lsp4j.services.LanguageClient;

@Data
@Builder
public class LanguageServerContext {
    private LanguageClient client;
}
