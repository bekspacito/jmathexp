package edu.myrza.jmathexp.expression_unit.variable;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnit;

import java.util.Map;


public class Variable implements ExpressionUnit{

    private final String variable;
    private double value;

    private static ExpUnitType type;

    static {
        type = ExpUnitType.VARIABLE;
    }

    public Variable(String variable,double value){
        this.variable = variable;
        this.value = value;
    }

    public void update(double newValue){
        this.value = newValue;
    }

    public double evaluate(double ... args) {
        return value;
    }

    public int getArgc() {
        throw new UnsupportedOperationException("variables have no args...");
    }

    public boolean isLeftAssociative() {
        throw new UnsupportedOperationException("variables have no association...");
    }

    public int getPrecedence() {
        throw new UnsupportedOperationException("variables don't have precedence....");
    }

    public ExpUnitType getType() {
        return type;
    }

}