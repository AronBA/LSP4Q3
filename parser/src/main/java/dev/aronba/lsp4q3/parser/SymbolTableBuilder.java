package dev.aronba.lsp4q3.parser;

public class SymbolTableBuilder extends Q3BaseVisitor<Void> {
    private Scope currentScope = new Scope(null);

    public Scope getGlobalScope() {
        return currentScope;
    }

    @Override
    public Void visitFunctionDeclaration(Q3Parser.FunctionDeclarationContext ctx) {
        String functionName = ctx.Identifier().getText();
        Symbol func = new Symbol(functionName, "function", ctx.start.getLine(), ctx.start.getCharPositionInLine());
        currentScope.define(func);


        Scope fnScope = new Scope(currentScope);
        Scope old = currentScope;
        currentScope = fnScope;
        super.visitFunctionDeclaration(ctx);

        currentScope = old;
        return null;
    }

    @Override
    public Void visitVariableDeclaration(Q3Parser.VariableDeclarationContext ctx) {
        String name = ctx.Identifier().getText();
        Symbol var = new Symbol(name, "variable", ctx.start.getLine(), ctx.start.getCharPositionInLine());
        currentScope.define(var);
        return super.visitVariableDeclaration(ctx);
    }
}
