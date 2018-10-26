package edu.myrza.jmathexp.expression_unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BuiltInOperators {

    public static final int ADDITION_PRECEDENCE = 500;
    public static final int SUBTRACTION_PRECEDENCE = ADDITION_PRECEDENCE;

    public static final int MULTIPLICATION_PRECEDENCE = 1000;
    public static final int DIVISION_PRECEDENCE = MULTIPLICATION_PRECEDENCE;
    public static final int MODULO_PRECEDENCE  = MULTIPLICATION_PRECEDENCE;

    public static final int UNARY_MINUS_PRECEDENCE = 5000;
    public static final int UNARY_PLUS_PRECEDENCE = UNARY_MINUS_PRECEDENCE;

    public static final int POWER_PRECEDENCE = 10000;

    private static Map<String,ExpressionUnit> globalBinaryOperators = new HashMap<>();
    private static Map<String,ExpressionUnit> globalUnaryOperators  = new HashMap<>();

    static {

        globalBinaryOperators.put("+",new BinaryOperator("+",true,ADDITION_PRECEDENCE,(operand1, operand2) -> operand1 + operand2 ));
        globalBinaryOperators.put("-",new BinaryOperator("-",true, SUBTRACTION_PRECEDENCE,(operand1, operand2) -> operand1 - operand2 ));

        globalBinaryOperators.put("*",new BinaryOperator("*",true,MULTIPLICATION_PRECEDENCE,(operand1, operand2) -> operand1 * operand2 ));
        globalBinaryOperators.put("/",new BinaryOperator("/",true,DIVISION_PRECEDENCE,(operand1, operand2) -> operand1 / operand2 ));
        globalBinaryOperators.put("%",new BinaryOperator("%",true,MODULO_PRECEDENCE,(operand1, operand2) -> operand1 % operand2 ));

        globalBinaryOperators.put("^",new BinaryOperator("^",false,POWER_PRECEDENCE,Math::pow));

        globalUnaryOperators.put("-",new UnaryOperator("-",false,UNARY_MINUS_PRECEDENCE, operand -> -1*operand));
        globalUnaryOperators.put("+",new UnaryOperator("+",false,UNARY_PLUS_PRECEDENCE, operand -> operand < 0 ? -1*operand : operand));
    }

    public static ExpressionUnit getBinaryOperator(String name){
        return globalBinaryOperators.get(name);
    }

    public static ExpressionUnit getUnaryOperator(String name){
        return globalUnaryOperators.get(name);
    }

    public static Set<String> getBinaryOperatorNames(){
        return globalBinaryOperators.keySet();
    }

    public static Set<String> getUnaryOperatorNames(){ return globalUnaryOperators.keySet(); }

}