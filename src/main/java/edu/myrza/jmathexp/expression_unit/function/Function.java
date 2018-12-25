package edu.myrza.jmathexp.expression_unit.function;

public class Function{

    private FunctionsBody impl;
    private String lexeme;
    private int argc;


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

    public String getLexeme() {
         return lexeme;
    }

    @Override
    public String toString() {
        return "Function{" +
                "lexeme='" + lexeme + '\'' +
                '}';
    }
}