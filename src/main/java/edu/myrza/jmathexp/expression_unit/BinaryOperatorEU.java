package edu.myrza.jmathexp.expression_unit;

public class BinaryOperatorEU implements ExpressionUnit{

    private BinaryOperator impl;
    private int precedence;
    private String name;

    private static EUType type;
    private static boolean isLeftAssociative;
    private static int argc;

    static {
        type = EUType.BINARY_OPERATOR;
        isLeftAssociative = true;
        argc = 2;
    }

    public BinaryOperatorEU(String name,int precedence,BinaryOperator impl){

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

    public EUType getType(){
        return type;
    }

    @Override
    public String toString() {
        return "BinaryOperatorEU{" +
                "name='" + name + '\'' +
                '}';
    }
}