package edu.myrza.jmathexp.expression_unit;

public class BinaryOperator implements ExpressionUnit{

    private BinaryAction impl;
    private int precedence;
    private String name;
    private boolean isLeftAssociative;

    private static ExpUnitType type;

    private static int argc;

    static {
        type = ExpUnitType.BINARY_OPERATOR;
        argc = 2;
    }

    public BinaryOperator(String name,boolean isLeftAssociative,int precedence, BinaryAction impl){

        this.isLeftAssociative = isLeftAssociative;
        this.name = name;
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
                "name='" + name + '\'' +
                '}';
    }
}