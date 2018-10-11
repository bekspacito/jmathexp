package edu.myrza.jmathexp.expression_unit;

class FunctionEU implements ExpressionUnit{

    private Function impl;
    private String name;
    private int argc;

    private static EUType type;

    static {
        type = EUType.FUNCTION;
    }

    public FunctionEU(String name,int argc,Function impl){

        this.impl = impl;
        this.name = name;
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

    public EUType getType(){
        return type;
    }

    @Override
    public String toString() {
        return "FunctionEU{" +
                "name='" + name + '\'' +
                '}';
    }
}