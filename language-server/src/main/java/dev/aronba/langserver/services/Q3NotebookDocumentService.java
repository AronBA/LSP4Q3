package dev.aronba.langserver.services;

import dev.aronba.langserver.LanguageServerContext;
import org.eclipse.lsp4j.DidChangeNotebookDocumentParams;
import org.eclipse.lsp4j.DidCloseNotebookDocumentParams;
import org.eclipse.lsp4j.DidOpenNotebookDocumentParams;
import org.eclipse.lsp4j.DidSaveNotebookDocumentParams;

public class Q3NotebookDocumentService implements org.eclipse.lsp4j.services.NotebookDocumentService {
    LanguageServerContext languageServerContext;
    public Q3NotebookDocumentService(LanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
    }

    @Override
    public void didOpen(DidOpenNotebookDocumentParams didOpenNotebookDocumentParams) {

    }

    @Override
    public void didChange(DidChangeNotebookDocumentParams didChangeNotebookDocumentParams) {

    }

    @Override
    public void didSave(DidSaveNotebookDocumentParams didSaveNotebookDocumentParams) {

    }

    @Override
    public void didClose(DidCloseNotebookDocumentParams didCloseNotebookDocumentParams) {

    }
}
