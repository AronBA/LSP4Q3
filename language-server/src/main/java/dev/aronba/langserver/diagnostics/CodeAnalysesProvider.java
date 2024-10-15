package dev.aronba.langserver.diagnostics;

import net.neostralis.q3.compiler.Q3Compiler;
import net.neostralis.q3.parsers.Line;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class CodeAnalysesProvider {

    public Q3Compiler.Result run(File file ){

        String code = "";
        try{
           code = Files.readString(file.toPath());
        }catch (IOException e){}

        List<Q3Compiler.Input> inputs = List.of(new Q3Compiler.Input(new Line.OriginFile(file), code));
        Q3Compiler.Result result = new Q3Compiler("LSP-COMPILER", inputs, true).run();

        return result;
    }

//    public static void main(String[] args) {
//        CodeAnalysesProvider codeAnalysesProvider = new CodeAnalysesProvider();
//        File file = new File("/home/aron/Development/Q3/main.q3");
//        System.out.println(codeAnalysesProvider.run(file, null).getWarnings().size());
//    }
    }

