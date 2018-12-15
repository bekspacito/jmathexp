package edu.myrza.jmathexp.expression_unit.variable;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnit;

import java.util.Map;

public class Variable implements ExpressionUnit{

    private String variable;
    private Map<String,Double> varValues;

    private static ExpUnitType type;

    static {
        type = ExpUnitType.VARIABLE;
    }

    public Variable(String variable,Map<String,Double> varValues){
        this.variable = variable;
        this.varValues = varValues;
    }

    public double evaluate(double ... args) {
        return varValues.get(variable);
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