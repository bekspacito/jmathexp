package edu.myrza.jmathexp.expression_unit;

import java.util.Set;

public interface ExpressionUnitFactory{

    ExpressionUnit create(ExpUnitType type, String name);

    ExpressionUnit convert(double number);

    /**
     * Returns a name set of available functions or operands.
     * */
    Set<String> getIds(ExpUnitType type);

}