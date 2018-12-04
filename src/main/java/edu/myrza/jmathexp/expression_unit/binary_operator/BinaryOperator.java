package edu.myrza.jmathexp.expression_unit.binary_operator;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnit;

public class BinaryOperator implements ExpressionUnit {

    private BinaryOperatorsBody impl;
    private int precedence;
    private String lexeme;
    private boolean isLeftAssociative;

    private static ExpUnitType type;

    private static int argc;

    static {
        type = ExpUnitType.BINARY_OPERATOR;
        argc = 2;
    }

    public BinaryOperator(String lexeme, boolean isLeftAssociative, int precedence, BinaryOperatorsBody impl){

        this.isLeftAssociative = isLeftAssociative;
        this.lexeme = lexeme;
        this.precedence = precedence;
        this.impl = impl;

    }

    public double evaluate(double... args) {

        if(args == null) throw new IllegalArgumentException("varargs can't be null : " + toString());
        if(args.length != getArgc()) throw new IllegalArgumentException("binary operator can't have more or less than " + getArgc() + " arg : " + toString());

        return impl.execute(args[0],args[1]);

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

    public ExpUnitType getType(){
        return type;
    }

    @Override
    public String toString() {
        return "BinaryOperator{" +
                "lexeme='" + lexeme + '\'' +
                '}';
    }
}