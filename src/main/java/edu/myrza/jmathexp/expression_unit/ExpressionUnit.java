package edu.myrza.jmathexp.expression_unit;

public interface ExpressionUnit{

    double  evaluate(double ... args);

    int     getArgc();

    int     getPrecedence();

    boolean isLeftAssociative();

    ExpressionUnits getType();
}