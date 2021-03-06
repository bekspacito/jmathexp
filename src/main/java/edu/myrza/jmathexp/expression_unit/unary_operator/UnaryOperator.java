package edu.myrza.jmathexp.expression_unit.unary_operator;


public class UnaryOperator {

    private UnaryOperatorsBody impl;
    private int precedence;
    private String lexeme;
    private boolean isLeftAssociative;

    private static int argc;

    static {
        argc = 1;
    }

    public UnaryOperator(String lexeme, boolean isLeftAssociative, int precedence, UnaryOperatorsBody impl){

        this.isLeftAssociative = isLeftAssociative;
        this.impl = impl;
        this.lexeme = lexeme;
        this.precedence = precedence;

    }

    public double evaluate(double... args) {

        if(args == null) throw new IllegalArgumentException("varargs can't be null : " + toString());
        if(args.length != getArgc()) throw new IllegalArgumentException("unary operator can't have more or less than one arg : " + toString());

        return impl.execute(args[0]);

    }

    public int getArgc(){
        return argc;
    }

    public boolean isLeftAssociative(){
        return isLeftAssociative;
    }

    public int getPrecedence(){
        return precedence;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "UnaryOperator{" +
                "lexeme='" + lexeme + '\'' +
                '}';
    }
}