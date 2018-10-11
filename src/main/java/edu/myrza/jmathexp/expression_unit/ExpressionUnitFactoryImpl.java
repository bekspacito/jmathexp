package edu.myrza.jmathexp.expression_unit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ExpressionUnitFactoryImpl implements ExpressionUnitFactory{

    private Map<String,FunctionEU>          customFunctions;
    private Map<String,BinaryOperatorEU>    customBinaryOperators;
    private Map<String,UnaryOperatorEU>     customUnaryOperators;


    public ExpressionUnitFactoryImpl(Map<String,FunctionEU> customFunctions,
                                     Map<String,BinaryOperatorEU> customBinaryOperators,
                                     Map<String,UnaryOperatorEU> customUnaryOperators)
    {
        this.customFunctions = customFunctions;
        this.customBinaryOperators = customBinaryOperators;
        this.customUnaryOperators = customUnaryOperators;
    }

    /**
     * Both global and local functions/operators are merged here
     * */
    @Override
    public ExpressionUnit createExpressionUnit(EUType type, String id) {

        switch (type){

            case FUNCTION        : return getLocalOrGlobalFunc(id);
            case UNARY_OPERATOR  : return getLocalOrGlobalUnOp(id);
            case BINARY_OPERATOR : return getLocalOrGlobalBinOp(id);
            case OPERAND         :
            default              : throw new IllegalArgumentException("cannot create an ExpressionUnit of given type : " + type);

        }
    }

    @Override
    public ExpressionUnit convertToOperand(double number) {
       return new OperandEU(number);
    }

    /**
     * Both global and local functions/operators are merged here
     * */
    @Override
    public Set<String> getIds(EUType type) {

        switch (type){
            case FUNCTION:{
                Set<String> funcIds = new HashSet<>();

                if(customFunctions != null)
                    funcIds.addAll(customFunctions.keySet());
                funcIds.addAll(GlobalFunctions.getFunctionNames());

                return funcIds;
            }
            case BINARY_OPERATOR:{
                Set<String> binOpIds = new HashSet<>();

                if(customBinaryOperators != null)
                    binOpIds.addAll(customBinaryOperators.keySet());
                binOpIds.addAll(GlobalOperators.getBinaryOperatorNames());

                return binOpIds;
            }
            case UNARY_OPERATOR:{
                Set<String> unOpIds = new HashSet<>();

                if(customUnaryOperators != null)
                    unOpIds.addAll(customUnaryOperators.keySet());
                unOpIds.addAll(GlobalOperators.getUnaryOperatorNames());

                return unOpIds;
            }
            default:{
                throw new IllegalArgumentException("given Expression Unit type can't have any ids : " + type);
            }
        }

    }

    private ExpressionUnit getLocalOrGlobalFunc(String id){

        ExpressionUnit reqFunc = null;

        if (customFunctions != null)
            reqFunc = customFunctions.get(id);
        if (reqFunc == null)
            reqFunc = GlobalFunctions.getFunction(id);
        if (reqFunc == null)
            throw new IllegalArgumentException("no such function with id : " + id);

        return reqFunc;
    }

    private ExpressionUnit getLocalOrGlobalBinOp(String id){

        ExpressionUnit binaryOp = null;

        if (customBinaryOperators != null)
            binaryOp = customBinaryOperators.get(id);
        if (binaryOp == null)
            binaryOp = GlobalOperators.getBinaryOperator(id);
        if (binaryOp == null)
            throw new IllegalArgumentException("no such binary operator with id : " + id);

        return binaryOp;
    }

    private ExpressionUnit getLocalOrGlobalUnOp(String id){

        ExpressionUnit unaryOp = null;

        if (customUnaryOperators != null)
            unaryOp = customUnaryOperators.get(id);
        if (unaryOp == null)
            unaryOp = GlobalOperators.getUnaryOperator(id);
        if (unaryOp == null)
            throw new IllegalArgumentException("no such unary operator with id : " + id);
        return unaryOp;

    }
}