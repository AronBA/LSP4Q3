import dev.aronba.langserver.Q3LanguageServer;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Q3LanguageServerTest {

    private Q3LanguageServer q3LanguageServer;

    @BeforeEach
    public void setUp() {
        q3LanguageServer = new Q3LanguageServer();
    }

    @Test
    void testExitWithShutdown() {
        assertDoesNotThrow(() -> {
                    q3LanguageServer.shutdown();
                }
        );
        assertEquals(0, q3LanguageServer.getErrorCode());
    }

    @Test
    void testExitWithoutShutdown() {
        assertEquals(1, q3LanguageServer.getErrorCode());
    }

}