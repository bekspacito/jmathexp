package edu.myrza.jmathexp.expression_unit;

public class OperandEU implements ExpressionUnit{

    private double value;

    private static ExpressionUnits type;

    static {
        type = ExpressionUnits.OPERAND;
    }

    public OperandEU(double value){
        this.value = value;
    }

    public double evaluate(double ... args) {
        return value;
    }

    public int getArgc() {
        throw new UnsupportedOperationException("operands have no args...");
    }

    public boolean isLeftAssociative() {
        throw new UnsupportedOperationException("operands have no association...");
    }

    public int getPrecedence() {
        throw new UnsupportedOperationException("operands don't have precedence");
    }

    public ExpressionUnits getType() {
        return type;
    }
}