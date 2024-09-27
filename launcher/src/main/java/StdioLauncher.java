import dev.aronba.langserver.Q3LanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StdioLauncher {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LogManager.getLogManager().reset();
        Logger gloabalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        gloabalLogger.setLevel(Level.OFF);

        startServer(System.in, System.out);

    }

    private static void startServer(InputStream inputStream, OutputStream outputStream) throws ExecutionException, InterruptedException {

        Q3LanguageServer server = new Q3LanguageServer();
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, inputStream, outputStream);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);

        Future<?> startListening = launcher.startListening();
        startListening.get();

    }
}
