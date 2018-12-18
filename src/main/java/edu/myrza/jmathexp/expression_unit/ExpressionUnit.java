package edu.myrza.jmathexp.expression_unit;

/**
 * Expression unit represents atomic parts of any math expression from which we can evaluate
 * a value e.g. variable,operand,function etc. On the contrary, we cannot evaluate open parentheses
 * or function argument separator
 * */

public interface ExpressionUnit{

    double  evaluate(double ... args);

    int     getArgc();

    int     getPrecedence();

    boolean isLeftAssociative();

    ExpUnitType getType();
}