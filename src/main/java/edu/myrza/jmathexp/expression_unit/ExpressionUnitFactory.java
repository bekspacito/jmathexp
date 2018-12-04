package edu.myrza.jmathexp.expression_unit;

import java.util.Set;

public interface ExpressionUnitFactory{

    ExpressionUnit create(ExpUnitType type, String name);
    /**
     * Returns a name set of available functions or operands.
     * */
    Set<String> getLexemes(ExpUnitType type);

}