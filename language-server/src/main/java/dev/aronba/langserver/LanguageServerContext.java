package dev.aronba.langserver;

import lombok.Builder;
import lombok.Data;
import org.eclipse.lsp4j.services.LanguageClient;

@Builder
@Data
public class LanguageServerContext {


    LanguageClient client;
}
