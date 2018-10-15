package edu.myrza.jmathexp.expression_unit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionUnitFactoryImpl implements ExpressionUnitFactory{

    private Map<String,Function>          customFunctions;
    private Map<String,BinaryOperator>    customBinaryOperators;
    private Map<String,UnaryOperator>     customUnaryOperators;


    public ExpressionUnitFactoryImpl(Map<String,Function> customFunctions,
                                     Map<String,BinaryOperator> customBinaryOperators,
                                     Map<String,UnaryOperator> customUnaryOperators)
    {
        this.customFunctions = customFunctions;
        this.customBinaryOperators = customBinaryOperators;
        this.customUnaryOperators = customUnaryOperators;
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