package edu.myrza.jmathexp.expression_unit;

import java.util.Set;

public interface ExpressionUnitFactory{

    ExpressionUnit createExpressionUnit(EUType type, String name);

    ExpressionUnit convertToOperand(double number);

    /**
     * Returns a name set of available functions or operands.
     * */
    Set<String> getIds(EUType type);

}