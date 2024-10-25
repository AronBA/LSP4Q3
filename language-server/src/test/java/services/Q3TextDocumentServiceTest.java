package services;

import dev.aronba.langserver.services.Q3TextDocumentService;
import dev.aronba.langserver.utils.LanguageServerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Q3TextDocumentServiceTest {


    private Q3TextDocumentService q3TextDocumentService;
    private LanguageServerContext context;

    @BeforeEach
    void setup(){
        this.context = LanguageServerContext.builder().build();
        this.q3TextDocumentService = new Q3TextDocumentService(context);
    }



}
