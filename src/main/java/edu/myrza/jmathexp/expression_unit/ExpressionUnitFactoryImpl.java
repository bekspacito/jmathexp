package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperator;
import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperatorsBody;
import edu.myrza.jmathexp.expression_unit.function.Function;
import edu.myrza.jmathexp.expression_unit.function.FunctionsBody;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperator;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperatorsBody;

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

        public Builder addFunction(String token,int argc,FunctionsBody impl){

            if(token == null || token.equals("")) throw new IllegalArgumentException("tokenizer cannot be null or empty string....");
            if(argc <= 0)                         throw new IllegalArgumentException("argument amount cannot be less or equal zero....");
            if(impl == null)                      throw new IllegalArgumentException("math action itself didn't been supplied....");

            if(customFunctions == null)
                customFunctions = new HashMap<>();

            customFunctions.put(token,new Function(token,argc,impl));

            return this;
        }

        public Builder addUnaryOperator(String token,boolean isLeftAssociative,int precedence,UnaryOperatorsBody unaryOperatorsBody){

            if(token == null || token.equals("")) throw new IllegalArgumentException("tokenizer cannot be null or empty string....");
            if(precedence <= 0)                         throw new IllegalArgumentException("precedence cannot be less or equal zero....");
            if(unaryOperatorsBody == null)                      throw new IllegalArgumentException("math unary action itself didn't been supplied....");

            if(customUnaryOperators == null)
                customUnaryOperators = new HashMap<>();

            customUnaryOperators.put(token,new UnaryOperator(token,isLeftAssociative,precedence, unaryOperatorsBody));

            return this;

        }

        public Builder addBinaryOperator(String token,boolean isLeftAssociative,int precedence,BinaryOperatorsBody binaryOperatorsBody){

            if(token == null || token.equals("")) throw new IllegalArgumentException("tokenizer cannot be null or empty string....");
            if(precedence <= 0)                         throw new IllegalArgumentException("precedence cannot be less or equal zero....");
            if(binaryOperatorsBody == null)                      throw new IllegalArgumentException("math binary action itself didn't been supplied....");

            if(customBinaryOperators == null)
                customBinaryOperators = new HashMap<>();

            customBinaryOperators.put(token,new BinaryOperator(token,isLeftAssociative,precedence, binaryOperatorsBody));

            return this;
        }

        public ExpressionUnitFactory build(){ return new ExpressionUnitFactoryImpl(this); }

    }

    /**
     * Both global and local functions/operators are merged here
     * */
    @Override
    public ExpressionUnit find(ExpUnitType type, String lexeme) {

        switch (type){

            case FUNCTION        : return getExpressionUnit(lexeme,customFunctions,       BuiltInFunctions::getFunction,      "no such function of lexeme : " + lexeme);
            case UNARY_OPERATOR  : return getExpressionUnit(lexeme,customUnaryOperators,  BuiltInOperators::getUnaryOperator, "no such unary operator of lexeme : " + lexeme);
            case BINARY_OPERATOR : return getExpressionUnit(lexeme,customBinaryOperators, BuiltInOperators::getBinaryOperator,"no such binary operator of lexeme : " + lexeme);
            default              : throw new IllegalArgumentException("cannot find an ExpressionUnit of given type : " + type);

        }
    }

    /**
     * Both global and local functions/operators are merged here
     * */
    @Override
    public Set<String> getLexemes(ExpUnitType type) {

        switch (type){
            case FUNCTION         : return getLexemes(customFunctions      , BuiltInFunctions::getFunctionNames);
            case BINARY_OPERATOR  : return getLexemes(customBinaryOperators, BuiltInOperators::getBinaryOperatorNames);
            case UNARY_OPERATOR   : return getLexemes(customUnaryOperators , BuiltInOperators::getUnaryOperatorNames);
            default               : throw new IllegalArgumentException("given Expression Unit type can't have any ids : " + type);
        }

    }

    private ExpressionUnit getExpressionUnit(String lexeme,
                                             Map<String,? extends ExpressionUnit> customExpUnitsHolder,
                                             BuiltInExpUnitsHolder builtInExpUnitsHolder,
                                             String excMessage)
    {

        ExpressionUnit reqExpUnit = null;

        if (customExpUnitsHolder != null)
            reqExpUnit = customExpUnitsHolder.get(lexeme);
        if (reqExpUnit == null)
            reqExpUnit = builtInExpUnitsHolder.getExpUnit(lexeme);
        if (reqExpUnit == null)
            throw new IllegalArgumentException(excMessage);

        return reqExpUnit;

    }

    private Set<String> getLexemes(Map<String,? extends ExpressionUnit> customExpUnitsHolder,
                                   BuiltInExpUnitLexemesHolder builtInExpUnitIdsHolder)
    {

        Set<String> lexemes = new HashSet<>();

        if(customExpUnitsHolder != null)
            lexemes.addAll(customExpUnitsHolder.keySet());
        lexemes.addAll(builtInExpUnitIdsHolder.getLexemes());

        return lexemes;

    }

    private interface BuiltInExpUnitsHolder {
        ExpressionUnit getExpUnit(String name);
    }

    private interface BuiltInExpUnitLexemesHolder {
        Set<String> getLexemes();
    }
}