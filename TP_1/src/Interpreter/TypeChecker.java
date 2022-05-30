package Interpreter;
import SymbolTable.FunctionSymbol;
import SymbolTable.Scope;
import SymbolTable.Symbol;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class TypeChecker extends a22BaseListener{

    public Scope globalScope;
    public Scope currentScope;
    public FunctionSymbol currentFunction;
    public int semanticErrors;

    public boolean hasMain = false;
    public boolean inLoop = false;


    public ParseTreeProperty<String> exprType = new ParseTreeProperty<>();

    public ParseTreeProperty<Scope> scopes = new ParseTreeProperty<>();



    private boolean defineSymbol(ParserRuleContext ctx, Symbol S){

        if(S == null) return false;

        Symbol symbol = this.currentScope.resolve(S.lexeme());
        if(symbol == null){
            this.currentScope.define(S);
            return true;
        }

        if(symbol instanceof FunctionSymbol)
            System.err.println("Line: " + ctx.start.getLine() + "; Redefining previously defined function " + symbol.lexeme());
        else
            System.err.println("Line: " + ctx.start.getLine() + "; Redefining previously defined variable " + symbol.lexeme());

        this.semanticErrors++;
        return false;
    }
    @Override
    public void enterStart(a22.StartContext ctx) {
        globalScope = new Scope(null, "GLOBAL");
        currentScope = globalScope;
        scopes.put(ctx, currentScope);
        this.semanticErrors = 0;
        Symbol.TypesList.add("int");
        Symbol.TypesList.add("real");
        Symbol.TypesList.add("string");
        Symbol.TypesList.add("bool");
        Symbol.TypesList.add("void");


    }

    @Override
    public void exitStart(a22.StartContext ctx) {
        if(!hasMain){
            System.out.println("COMPILATION ERROR : a22 file must have a main function.");
            semanticErrors++;
        }
    }

    @Override
    public void enterFile(a22.FileContext ctx) { }

    @Override
    public void exitFile(a22.FileContext ctx) {

    }

    @Override
    public void enterVariable(a22.VariableContext ctx) {

    }

    @Override
    public void exitVariable(a22.VariableContext ctx) {

    }

    @Override
    public void enterVariable_declaration(a22.Variable_declarationContext ctx) {



    }
    @Override
    public void exitVariable_declaration(a22.Variable_declarationContext ctx) {
        List<TerminalNode>  tns = ctx.IDENTIFIER();
        try{
            ctx.primitive_data_type();
            String type = ctx.primitive_data_type().getText();
            for( TerminalNode t : tns){

                Symbol symbol = switch (type) {
                    case "int" -> new Symbol(t.getSymbol(), "int");
                    case "real" -> new Symbol(t.getSymbol(), "real");
                    case "string" -> new Symbol(t.getSymbol(), "string");
                    case "bool" -> new Symbol(t.getSymbol(), "bool");
                    default -> null;
                };

                defineSymbol(ctx, symbol);
            }

        }catch (NullPointerException e) {           // Non primitive type
            String type = ctx.IDENTIFIER(0).getText();

            if(!Symbol.TypesList.contains(type)){
                semanticErrors++;
                System.err.println("Line: " + ctx.start.getLine() + "; Type: " + type +"not defined" );

            }
            for( TerminalNode t : tns){
                defineSymbol(ctx, new Symbol(t.getSymbol(), type));

            }

        }
        System.out.println("scope: " + this.currentScope );
    }

    @Override
    public void enterBrackets(a22.BracketsContext ctx) {

    }

    @Override
    public void exitBrackets(a22.BracketsContext ctx) {

    }

    @Override
    public void enterVariable_initialization(a22.Variable_initializationContext ctx) {

        String variableName1 = ctx.assignment().expression(0).getText();
        String variableName2 = ctx.assignment().expression(1).getText();

        System.out.println("identifier: " + variableName1);
        if(this.currentScope.resolve(variableName1) != null){
            System.err.println("" +
                    "Duplicated variable: " + variableName1
                    + " in line " + ctx.assignment().getStart().getLine());
            semanticErrors++;
            return;
        }

        String type = "invalid";
        if(ctx.primitive_data_type() != null)
            type = ctx.primitive_data_type().getText();
        else
            type = ctx.IDENTIFIER().getText();


        defineSymbol(ctx, new Symbol(ctx.assignment().expression(0).start, type));
        System.out.println("sada: " + variableName2);
    }

    @Override
    public void exitVariable_initialization(a22.Variable_initializationContext ctx) {

    }

    @Override
    public void enterPrimitive_data_type(a22.Primitive_data_typeContext ctx) {

    }

    @Override
    public void exitPrimitive_data_type(a22.Primitive_data_typeContext ctx) {

    }

    @Override
    public void enterComposite_data_type_def(a22.Composite_data_type_defContext ctx) { }

    @Override
    public void exitComposite_data_type_def(a22.Composite_data_type_defContext ctx) {
        TerminalNode  t = ctx.IDENTIFIER();
        Symbol.addType(t.getText());
        //System.out.println("symbol types: " + Symbol.TypesList.toString());
    }


    // ============================ DATA TYPES ===================
    @Override
    public void enterInt_literal(a22.Int_literalContext ctx) { }
    @Override
    public void enterReal_literal(a22.Real_literalContext ctx) { }
    @Override
    public void enterString_literal(a22.String_literalContext ctx) { }
    @Override
    public void enterFalse(a22.FalseContext ctx) { }
    @Override
    public void enterTrue(a22.TrueContext ctx) { }


    @Override
    public void exitInt_literal(a22.Int_literalContext ctx) {
        exprType.put(ctx, "int");
    }

    @Override
    public void exitReal_literal(a22.Real_literalContext ctx) {
        exprType.put(ctx, "real");
    }

    @Override
    public void exitString_literal(a22.String_literalContext ctx) {
        exprType.put(ctx, "string");
    }

    @Override
    public void exitTrue(a22.TrueContext ctx) {
        exprType.put(ctx, "bool");
    }

    @Override
    public void exitFalse(a22.FalseContext ctx) {
        exprType.put(ctx, "bool");
    }

    // =============================


    @Override
    public void enterIdentifier(a22.IdentifierContext ctx) {

    }

    @Override
    public void exitIdentifier(a22.IdentifierContext ctx) {
        exprType.put(ctx, "string");
    }

    @Override
    public void enterFun_call(a22.Fun_callContext ctx) {

    }

    @Override
    public void exitFun_call(a22.Fun_callContext ctx) {

    }

    @Override
    public void enterSimple_exp(a22.Simple_expContext ctx) { }

    @Override
    public void exitSimple_exp(a22.Simple_expContext ctx) {
        exprType.put(ctx, exprType.get(ctx.simple_expression().getChild(0)));
    }

    @Override
    public void enterPm_exp(a22.Pm_expContext ctx) {

    }

    @Override
    public void exitPm_exp(a22.Pm_expContext ctx) {

    }

    @Override
    public void enterEqual_exp(a22.Equal_expContext ctx) {

    }

    @Override
    public void exitEqual_exp(a22.Equal_expContext ctx) {

    }

    @Override
    public void enterIndex_exp(a22.Index_expContext ctx) {

    }

    @Override
    public void exitIndex_exp(a22.Index_expContext ctx) {

    }

    @Override
    public void enterParen_exp(a22.Paren_expContext ctx) {

    }

    @Override
    public void exitParen_exp(a22.Paren_expContext ctx) {

    }

    @Override
    public void enterMember_acc_exp(a22.Member_acc_expContext ctx) {

    }

    @Override
    public void exitMember_acc_exp(a22.Member_acc_expContext ctx) {

    }

    @Override
    public void enterLlgg_exp(a22.Llgg_expContext ctx) {

    }

    @Override
    public void exitLlgg_exp(a22.Llgg_expContext ctx) {

    }

    @Override
    public void enterNegation_exp(a22.Negation_expContext ctx) {

    }

    @Override
    public void exitNegation_exp(a22.Negation_expContext ctx) {

    }

    @Override
    public void enterMdr_exp(a22.Mdr_expContext ctx) {

    }

    @Override
    public void exitMdr_exp(a22.Mdr_expContext ctx) {

    }

    @Override
    public void enterAnd_exp(a22.And_expContext ctx) {

    }

    @Override
    public void exitAnd_exp(a22.And_expContext ctx) {

    }

    @Override
    public void enterOr_exp(a22.Or_expContext ctx) {

    }

    @Override
    public void exitOr_exp(a22.Or_expContext ctx) {

    }

    @Override
    public void enterFunction_def(a22.Function_defContext ctx) {
        List<TerminalNode> tns = ctx.IDENTIFIER();

        String name = "";
        if(ctx.primitive_data_type() != null || ctx.VOID() != null)
            name = ctx.IDENTIFIER(0).getText();
        else
            name = ctx.IDENTIFIER(1).getText();


        if(this.globalScope.resolve(name) != null){
            System.err.println("" +
                    "Already defined the name on a Variable or a Function :" + name
                    + " in line " + ctx.IDENTIFIER(0).getSymbol().getLine());
            semanticErrors++;
            return;
        }

        this.currentScope = new Scope(this.globalScope, name);
        scopes.put(ctx, this.globalScope);
    }




    @Override
    public void exitFunction_def(a22.Function_defContext ctx) {
        List<TerminalNode> tns = ctx.IDENTIFIER();


        if(ctx.primitive_data_type() != null){
            String type = ctx.primitive_data_type().getText();
            currentFunction = new FunctionSymbol(ctx.IDENTIFIER(0).getSymbol(), type);
        }
        else if(!Symbol.TypesList.contains(ctx.IDENTIFIER(0).getText())){
            System.err.println("Undefiend type on line " + ctx.IDENTIFIER(0).getSymbol().getLine());
            semanticErrors++;
            return;
        }
        else{
            String type = ctx.IDENTIFIER(0).getText();
            currentFunction = new FunctionSymbol(ctx.IDENTIFIER(1).getSymbol(), type);
        }

        for(TerminalNode tn : tns){
            if(tn.getText().equals("main")){
                if(hasMain){
                    System.err.println("Duplicated Main function in line: " + tn.getSymbol().getLine());
                    semanticErrors++;
                    return;
                }
                hasMain = true;
                break;
            }
        }
    }

    @Override
    public void enterFunction_def_args(a22.Function_def_argsContext ctx) {

    }

    @Override
    public void exitFunction_def_args(a22.Function_def_argsContext ctx) {

    }

    @Override
    public void enterFunction_def_args_types(a22.Function_def_args_typesContext ctx) {

    }

    @Override
    public void exitFunction_def_args_types(a22.Function_def_args_typesContext ctx) {

    }

    @Override
    public void enterNormal_fun_call(a22.Normal_fun_callContext ctx) {
        System.out.println(" enter normal fun cal");

        String functionName = ctx.IDENTIFIER().getText();
        Symbol f =  this.currentScope.resolve(functionName);

        if(f == null){
            System.err.println("" +
                    "Undefined function " + functionName
                    + " in line " + ctx.IDENTIFIER().getSymbol().getLine());
            semanticErrors++;
            return;
        }
        if(!(f instanceof FunctionSymbol)){
            System.err.println("" +
                    "Using variable " + functionName
                    + " as function in line " + ctx.IDENTIFIER().getSymbol().getLine());
            semanticErrors++;
            return;
        }

        this.currentFunction = (FunctionSymbol) f;
        this.currentScope = new Scope(this.currentScope);
        scopes.put(ctx, currentScope);
    }

    @Override
    public void exitNormal_fun_call(a22.Normal_fun_callContext ctx) {

        System.out.println(" exit normal fun cal");

    }

    @Override
    public void enterWrite_fun_call(a22.Write_fun_callContext ctx) {

    }

    @Override
    public void exitWrite_fun_call(a22.Write_fun_callContext ctx) {

    }

    @Override
    public void enterRead_fun_call(a22.Read_fun_callContext ctx) {

    }

    @Override
    public void exitRead_fun_call(a22.Read_fun_callContext ctx) {

    }

    @Override
    public void enterFunction_call_args(a22.Function_call_argsContext ctx) {

    }

    @Override
    public void exitFunction_call_args(a22.Function_call_argsContext ctx) {

    }

    @Override
    public void enterAssignment_state(a22.Assignment_stateContext ctx) {

    }

    @Override
    public void exitAssignment_state(a22.Assignment_stateContext ctx) {
        //exprType.get(ctx, exprType.get(ctx.assignment().expression(0).ç));
    }

    @Override
    public void enterConditional_state(a22.Conditional_stateContext ctx) {

    }

    @Override
    public void exitConditional_state(a22.Conditional_stateContext ctx) {

    }

    @Override
    public void enterWhile_state(a22.While_stateContext ctx) {

    }

    @Override
    public void exitWhile_state(a22.While_stateContext ctx) {

    }

    @Override
    public void enterFor_state(a22.For_stateContext ctx) {

    }

    @Override
    public void exitFor_state(a22.For_stateContext ctx) {

    }

    @Override
    public void enterControl_state(a22.Control_stateContext ctx) {

    }

    @Override
    public void exitControl_state(a22.Control_stateContext ctx) {

    }

    @Override
    public void enterExpression_state(a22.Expression_stateContext ctx) {

    }

    @Override
    public void exitExpression_state(a22.Expression_stateContext ctx) {

    }

    @Override
    public void enterBlock_state(a22.Block_stateContext ctx) {

    }

    @Override
    public void exitBlock_state(a22.Block_stateContext ctx) {

    }

    @Override
    public void enterSemicolon_state(a22.Semicolon_stateContext ctx) {

    }

    @Override
    public void exitSemicolon_state(a22.Semicolon_stateContext ctx) {

    }

    @Override
    public void enterAssignment(a22.AssignmentContext ctx) {

    }

    @Override
    public void exitAssignment(a22.AssignmentContext ctx) {

    }

    @Override
    public void enterConditional(a22.ConditionalContext ctx) {

    }

    @Override
    public void exitConditional(a22.ConditionalContext ctx) {

    }

    @Override
    public void enterWhile_loop(a22.While_loopContext ctx) {
        inLoop = true;
    }

    @Override
    public void exitWhile_loop(a22.While_loopContext ctx) {
        inLoop = false;
    }

    @Override
    public void enterFor_loop(a22.For_loopContext ctx) {
        inLoop = true;
    }

    @Override
    public void exitFor_loop(a22.For_loopContext ctx) {
        inLoop = false;
    }

    @Override
    public void enterControl(a22.ControlContext ctx) {

    }

    @Override
    public void exitControl(a22.ControlContext ctx) {

    }

    @Override
    public void enterBlock_statement(a22.Block_statementContext ctx) {

    }

    @Override
    public void exitBlock_statement(a22.Block_statementContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
