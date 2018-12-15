package edu.myrza.jmathexp.expression_unit;

public enum ExpUnitType {
    UNARY_OPERATOR,BINARY_OPERATOR,FUNCTION,OPERAND,VARIABLE;

    @Override
    public String toString() {

        String tmp = "ExpUnitType.";

        switch (this){

            case VARIABLE:return tmp + "VARIABLE";
            case OPERAND: return tmp + "OPERAND";
            case BINARY_OPERATOR: return tmp + "BINARY_OPERATOR";
            case UNARY_OPERATOR: return tmp + "UNARY_OPERATOR";
            case FUNCTION: return tmp + "FUNCTION";
            default: return null;
        }
    }
}