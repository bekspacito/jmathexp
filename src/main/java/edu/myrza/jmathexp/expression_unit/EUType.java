package edu.myrza.jmathexp.expression_unit;

public enum EUType {
    UNARY_OPERATOR,BINARY_OPERATOR,FUNCTION,OPERAND;

    @Override
    public String toString() {

        String tmp = "EUType.";

        switch (this){

            case OPERAND: return tmp + "OPERAND";
            case BINARY_OPERATOR: return tmp + "BINARY_OPERATOR";
            case UNARY_OPERATOR: return tmp + "UNARY_OPERATOR";
            case FUNCTION: return tmp + "FUNCTION";
            default: return null;
        }
    }
}