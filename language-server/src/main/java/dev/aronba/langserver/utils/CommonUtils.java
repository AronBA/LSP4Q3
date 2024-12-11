package dev.aronba.langserver.utils;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonUtils {

    public static Path uriToPath(String pathUri) {
        URI uri = URI.create(pathUri);
        return Paths.get(uri);
    }
}
