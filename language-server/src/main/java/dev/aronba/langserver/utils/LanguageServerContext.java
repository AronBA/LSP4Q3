package dev.aronba.langserver.utils;

import dev.aronba.langserver.buffer.BufferedWorkspace;
import lombok.Builder;
import lombok.Data;
import org.eclipse.lsp4j.services.LanguageClient;

@Data
@Builder
public class LanguageServerContext {
    private LanguageClient client;
    private BufferedWorkspace workspace;
}
