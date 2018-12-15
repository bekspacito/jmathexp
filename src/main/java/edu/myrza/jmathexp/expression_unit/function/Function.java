package edu.myrza.jmathexp.expression_unit.function;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnit;

public class Function implements ExpressionUnit {

    private FunctionsBody impl;
    private String lexeme;
    private int argc;

    private static ExpUnitType type;

    static {
        type = ExpUnitType.FUNCTION;
    }

    public Function(String lexeme, int argc, FunctionsBody impl){

        this.impl = impl;
        this.lexeme = lexeme;
        this.argc = argc;

    }

    public double evaluate(double... args) {

        if(args == null) throw new IllegalArgumentException("varargs can't be null : " + toString());
        if(args.length != getArgc()) throw new IllegalArgumentException("function " + toString() + " can't have more or less than " + getArgc() + " arg : ");

        return impl.execute(args);

    }

    public int getArgc(){
        return argc;
    }

    public boolean isLeftAssociative(){
        throw new UnsupportedOperationException("functions don't have associations : " + toString());
    }

    public int getPrecedence(){
        throw new UnsupportedOperationException("functions don't have precedence : " + toString());
    }

    public ExpUnitType getType(){
        return type;
    }

    @Override
    public String toString() {
        return "Function{" +
                "lexeme='" + lexeme + '\'' +
                '}';
    }
}