package edu.myrza.jmathexp.expression_unit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionUnitFactoryImpl implements ExpressionUnitFactory{

    private Map<String,Function>          customFunctions;
    private Map<String,BinaryOperator>    customBinaryOperators;
    private Map<String,UnaryOperator>     customUnaryOperators;


    private ExpressionUnitFactoryImpl(Builder builder)
    {
        this.customFunctions       = builder.customFunctions;
        this.customBinaryOperators = builder.customBinaryOperators;
        this.customUnaryOperators  = builder.customUnaryOperators;
    }

    public static class Builder{

        private Map<String,Function>       customFunctions;
        private Map<String,BinaryOperator> customBinaryOperators;
        private Map<String,UnaryOperator>  customUnaryOperators;

        public Builder addFunction(String token,int argc,Action impl){

            if(token == null || token.equals("")) throw new IllegalArgumentException("tokenizer cannot be null or empty string....");
            if(argc <= 0)                         throw new IllegalArgumentException("argument amount cannot be less or equal zero....");
            if(impl == null)                      throw new IllegalArgumentException("math action itself didn't been supplied....");

            if(customFunctions == null)
                customFunctions = new HashMap<>();

            customFunctions.put(token,new Function(token,argc,impl));

            return this;
        }

        public Builder addUnaryOperator(String token,boolean isLeftAssociative,int precedence,UnaryAction unaryAction){

            if(token == null || token.equals("")) throw new IllegalArgumentException("tokenizer cannot be null or empty string....");
            if(precedence <= 0)                         throw new IllegalArgumentException("precedence cannot be less or equal zero....");
            if(unaryAction == null)                      throw new IllegalArgumentException("math unary action itself didn't been supplied....");

            if(customUnaryOperators == null)
                customUnaryOperators = new HashMap<>();

            customUnaryOperators.put(token,new UnaryOperator(token,isLeftAssociative,precedence,unaryAction));

            return this;

        }

        public Builder addBinaryOperator(String token,boolean isLeftAssociative,int precedence,BinaryAction binaryAction){

            if(token == null || token.equals("")) throw new IllegalArgumentException("tokenizer cannot be null or empty string....");
            if(precedence <= 0)                         throw new IllegalArgumentException("precedence cannot be less or equal zero....");
            if(binaryAction == null)                      throw new IllegalArgumentException("math binary action itself didn't been supplied....");

            if(customBinaryOperators == null)
                customBinaryOperators = new HashMap<>();

            customBinaryOperators.put(token,new BinaryOperator(token,isLeftAssociative,precedence,binaryAction));

            return this;
        }

        public ExpressionUnitFactory build(){ return new ExpressionUnitFactoryImpl(this); }

    }

    /**
     * Both global and local functions/operators are merged here
     * */
    @Override
    public ExpressionUnit create(ExpUnitType type, String id) {

        switch (type){

            case FUNCTION        : return getExpressionUnit(id,customFunctions,       BuiltInFunctions::getFunction,      "no such function of id : " + id);
            case UNARY_OPERATOR  : return getExpressionUnit(id,customUnaryOperators,  BuiltInOperators::getUnaryOperator, "no such unary operator of id : " + id);
            case BINARY_OPERATOR : return getExpressionUnit(id,customBinaryOperators, BuiltInOperators::getBinaryOperator,"no such binary operator of id : " + id);
            case OPERAND         :
            default              : throw new IllegalArgumentException("cannot create an ExpressionUnit of given type : " + type);

        }
    }

    @Override
    public ExpressionUnit convert(double number) {
       return new Operand(number);
    }

    /**
     * Both global and local functions/operators are merged here
     * */
    @Override
    public Set<String> getIds(ExpUnitType type) {

        switch (type){
            case FUNCTION         : return getIds(customFunctions      , BuiltInFunctions::getFunctionNames);
            case BINARY_OPERATOR  : return getIds(customBinaryOperators, BuiltInOperators::getBinaryOperatorNames);
            case UNARY_OPERATOR   : return getIds(customUnaryOperators , BuiltInOperators::getUnaryOperatorNames);
            default               : throw new IllegalArgumentException("given Expression Unit type can't have any ids : " + type);
        }

    }

    private ExpressionUnit getExpressionUnit(String id,
                                             Map<String,? extends ExpressionUnit> customExpUnitsHolder,
                                             BuiltInExpUnitsHolder builtInExpUnitsHolder,
                                             String excMessage)
    {

        ExpressionUnit reqExpUnit = null;

        if (customExpUnitsHolder != null)
            reqExpUnit = customExpUnitsHolder.get(id);
        if (reqExpUnit == null)
            reqExpUnit = builtInExpUnitsHolder.getExpUnit(id);
        if (reqExpUnit == null)
            throw new IllegalArgumentException(excMessage);

        return reqExpUnit;

    }

    private Set<String> getIds(Map<String,? extends ExpressionUnit> customExpUnitsHolder,
                               BuiltInExpUnitIdsHolder builtInExpUnitIdsHolder)
    {

        Set<String> ids = new HashSet<>();

        if(customExpUnitsHolder != null)
            ids.addAll(customExpUnitsHolder.keySet());
        ids.addAll(builtInExpUnitIdsHolder.getIds());

        return ids;

    }

    private interface BuiltInExpUnitsHolder {
        ExpressionUnit getExpUnit(String name);
    }

    private interface BuiltInExpUnitIdsHolder{
        Set<String> getIds();
    }
}