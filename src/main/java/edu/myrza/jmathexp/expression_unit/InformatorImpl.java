package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperator;
import edu.myrza.jmathexp.expression_unit.function.Function;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Informator is an Adapter which knows about both Tokens(Tokenizer,ShuntingYard) and ExpressionUnits(ExpressionUnitFactory)
 * .Because of Informator, neither of the sides The Informator works with know about each other
 * */

public class InformatorImpl implements Informator{

    private List<Function>       functions;
    private List<BinaryOperator> binaryOperators;
    private List<UnaryOperator>  unaryOperators;

    public InformatorImpl(List<Function>       functions,
                          List<BinaryOperator> binaryOperators,
                          List<UnaryOperator>  unaryOperators)
    {
        this.functions = (functions != null) ? functions : Collections.emptyList();
        this.binaryOperators = (binaryOperators != null) ? binaryOperators : Collections.emptyList();
        this.unaryOperators = (unaryOperators != null) ? unaryOperators : Collections.emptyList();
    }

    @Override
    public int funcArgc(Token function) {

        if(function.type != Token.Type.FUNCTION)
            throw new IllegalArgumentException("required : Token of Type FUNCTION\nfound : Token of Type " + function.type);

        return  Stream.concat(functions.stream(),BuiltInFunctions.getFunctions())
                       .filter(f -> f.getLexeme().equals(function.lexeme))
                       .findFirst()
                       .orElseThrow(() -> new IllegalArgumentException("No such function found..." + function))
                       .getArgc();

    }

    @Override
    public boolean isLeftAssociated(Token operator) {

        if(operator.type == Token.Type.BINARY_OPERATOR){
            return Stream.concat(binaryOperators.stream(),BuiltInOperators.getBinaryOperators())
                          .filter(bo -> bo.getLexeme().equals(operator.lexeme))
                          .findFirst()
                          .orElseThrow(() -> new IllegalArgumentException("No such binary operator found..."))
                          .isLeftAssociative();
        }
        else if(operator.type == Token.Type.LS_UNARY_OPERATOR ||
                operator.type == Token.Type.RS_UNARY_OPERATOR)
        {
            return Stream.concat(unaryOperators.stream(),BuiltInOperators.getUnaryOperators())
                            .filter(uo -> uo.getLexeme().equals(operator.lexeme))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("No such unary operator found..."))
                            .isLeftAssociative();
        }else {
            throw new IllegalArgumentException("required : Token of Type *_OPERATOR\nfound : Token of Type" + operator.type);
        }

    }

    @Override
    public int priority(Token operator) {

        if(operator.type == Token.Type.BINARY_OPERATOR){
            return Stream.concat(binaryOperators.stream(),BuiltInOperators.getBinaryOperators())
                    .filter(bo -> bo.getLexeme().equals(operator.lexeme))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No such binary operator found..."))
                    .getPrecedence();
        }
        else if(operator.type == Token.Type.LS_UNARY_OPERATOR ||
                operator.type == Token.Type.RS_UNARY_OPERATOR)
        {
            return Stream.concat(unaryOperators.stream(),BuiltInOperators.getUnaryOperators())
                    .filter(uo -> uo.getLexeme().equals(operator.lexeme))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No such unary operator found..."))
                    .getPrecedence();
        }else {
            throw new IllegalArgumentException("required : Token of Type *_OPERATOR\nfound : Token of Type" + operator.type);
        }

    }

    @Override
    public Set<String> lexemesOf(Token.Type type) {

        switch (type) {
            case BINARY_OPERATOR   : return Stream.concat(binaryOperators.stream(),BuiltInOperators.getBinaryOperators())
                                                    .map(bo -> bo.getLexeme())
                                                    .collect(Collectors.toSet());

            case FUNCTION          : return Stream.concat(functions.stream(),BuiltInFunctions.getFunctions())
                                                    .map(fun -> fun.getLexeme())
                                                    .collect(Collectors.toSet());



            case RS_UNARY_OPERATOR : return Stream.concat(unaryOperators.stream(),BuiltInOperators.getUnaryOperators())
                                                    .filter(uo -> uo.isLeftAssociative())
                                                    .map(uo -> uo.getLexeme())
                                                    .collect(toSet());


            case LS_UNARY_OPERATOR : return Stream.concat(unaryOperators.stream(),BuiltInOperators.getUnaryOperators())
                                                    .filter(uo -> !uo.isLeftAssociative())
                                                    .map(uo -> uo.getLexeme())
                                                    .collect(toSet());


            default                : {
                throw new IllegalArgumentException("lexemes of type " + type + " is unavailable....");
            }
        }
    }
}
