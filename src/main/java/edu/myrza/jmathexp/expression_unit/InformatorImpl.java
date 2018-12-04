package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Informator is an Adapter which knows about both Tokens(Tokenizer,ShuntingYard) and ExpressionUnits(ExpressionUnitFactory)
 * and because of Informator neither of the sides with which the Informator works don't know about each other
 * */

public class InformatorImpl implements Informator{

    private ExpressionUnitFactory factory;

    public InformatorImpl(ExpressionUnitFactory factory){
        this.factory = factory;
    }

    @Override
    public int funcArgc(Token function) {

        if(function.type != Token.Type.FUNCTION)
            throw new IllegalArgumentException("required : Token of Type FUNCTION\nfound : Token of Type " + function.type);

        ExpressionUnit res = factory.create(ExpUnitType.FUNCTION,function.lexeme);

        return res.getArgc();
    }

    @Override
    public boolean isLeftAssociated(Token operator) {
        if( operator.type != Token.Type.LS_UNARY_OPERATOR &&
            operator.type != Token.Type.RS_UNARY_OPERATOR &&
            operator.type != Token.Type.BINARY_OPERATOR)

            throw new IllegalArgumentException("required : Token of Type *_OPERATOR\nfound : Token of Type" + operator.type);

        if(operator.type == Token.Type.BINARY_OPERATOR)
            return factory.create(ExpUnitType.BINARY_OPERATOR,operator.lexeme).isLeftAssociative();
        else
            return factory.create(ExpUnitType.UNARY_OPERATOR,operator.lexeme).isLeftAssociative();

    }

    @Override
    public int priority(Token operator) {
        if( operator.type != Token.Type.LS_UNARY_OPERATOR &&
                operator.type != Token.Type.RS_UNARY_OPERATOR &&
                operator.type != Token.Type.BINARY_OPERATOR)

            throw new IllegalArgumentException("required : Token of Type *_OPERATOR\nfound : Token of Type" + operator.type);

        if(operator.type == Token.Type.BINARY_OPERATOR)
            return factory.create(ExpUnitType.BINARY_OPERATOR,operator.lexeme).getPrecedence();
        else
            return factory.create(ExpUnitType.UNARY_OPERATOR,operator.lexeme).getPrecedence();
    }

    @Override
    public Set<String> lexemesOf(Token.Type type) {

        switch (type) {
            case BINARY_OPERATOR   : return factory.getLexemes(ExpUnitType.BINARY_OPERATOR);
            case FUNCTION          : return factory.getLexemes(ExpUnitType.FUNCTION);
            case RS_UNARY_OPERATOR : return factory.getLexemes(ExpUnitType.UNARY_OPERATOR).stream()
                                                    .filter(eu -> factory.create(ExpUnitType.UNARY_OPERATOR, eu).isLeftAssociative())
                                                    .collect(toSet());
            case LS_UNARY_OPERATOR : return factory.getLexemes(ExpUnitType.UNARY_OPERATOR).stream()
                                                    .filter(eu -> !factory.create(ExpUnitType.UNARY_OPERATOR, eu).isLeftAssociative())
                                                    .collect(toSet());
            default                : {
                //todo it seems like we need some kind tokenFactory
                throw new IllegalArgumentException("tokens of type " + type + " is unavailable....");
            }
        }
    }
}
