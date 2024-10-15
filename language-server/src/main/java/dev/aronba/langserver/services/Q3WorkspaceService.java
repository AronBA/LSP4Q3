package dev.aronba.langserver.services;

import dev.aronba.langserver.LanguageServerContext;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;

public class Q3WorkspaceService implements org.eclipse.lsp4j.services.WorkspaceService {
    LanguageServerContext languageServerContext;
    public Q3WorkspaceService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {

    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams didChangeWatchedFilesParams) {

    }
}
