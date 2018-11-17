package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;

import java.util.*;

import static edu.myrza.jmathexp.common.Token.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.toSet;

//todo Handle errors
//todo change Tokenizer constructor so that it receives an Informator
//todo function names have to be sorted decreasingly by length
//todo RSO names have to be sorted decreasingly by priority of RSO operations
//todo LSO operations have to be sorted increasingly by priority of LSO operations

public class Tokenizer{

    private ExpressionUnitFactory factory;
    private final Set<String> functions;
    private final Set<String> binOpNames;
    private final Set<String> rsOpNames;
    private final Set<String> lsOpNames;
    private Map<Type,List<Type>> neighboursAllowedTypes;


    public Tokenizer(ExpressionUnitFactory factory){
        this.factory = factory;

        functions = factory.getIds(ExpUnitType.FUNCTION);
        binOpNames    = factory.getIds(ExpUnitType.BINARY_OPERATOR);

        rsOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                .filter(eu -> factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                .collect(toSet());

        lsOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                .filter(eu -> !factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                .collect(toSet());

        neighboursAllowedTypes = new HashMap<>();

        neighboursAllowedTypes.put(Type.START,                  asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        neighboursAllowedTypes.put(Type.LS_UNARY_OPERATOR,      asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        neighboursAllowedTypes.put(Type.BINARY_OPERATOR,        asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        neighboursAllowedTypes.put(Type.OPEN_PARENTHESES,       asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        neighboursAllowedTypes.put(Type.FUNCTION_ARG_SEPARATOR, asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));

        neighboursAllowedTypes.put(Type.OPERAND,                asList(Type.RS_UNARY_OPERATOR,Type.BINARY_OPERATOR,Type.CLOSE_PARENTHESES,Type.FUNCTION_ARG_SEPARATOR,Type.END));
        neighboursAllowedTypes.put(Type.RS_UNARY_OPERATOR,      asList(Type.RS_UNARY_OPERATOR,Type.BINARY_OPERATOR,Type.CLOSE_PARENTHESES,Type.FUNCTION_ARG_SEPARATOR,Type.END));
        neighboursAllowedTypes.put(Type.CLOSE_PARENTHESES,      asList(Type.RS_UNARY_OPERATOR,Type.BINARY_OPERATOR,Type.CLOSE_PARENTHESES,Type.FUNCTION_ARG_SEPARATOR,Type.END));

        neighboursAllowedTypes.put(Type.FUNCTION,               asList(Type.OPEN_PARENTHESES));
    }

    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty())
            throw new IllegalArgumentException("the math expression can neither be null nor be empty string...");

        LexicalAnalizer tokenSupplier = new LexicalAnalizer(exp,functions,rsOpNames,lsOpNames,binOpNames);
        List<Token> output = new LinkedList<>();
        Token current = new Token(Type.START,"[");
        List<Token> possibleNeighbours = null;
        NeighbourSeekingResult result = null;

        while (tokenSupplier.hasNext()){
            possibleNeighbours = tokenSupplier.next();
            result = findNeighbour(possibleNeighbours, neighboursAllowedTypes.get(current.type));

            if(!result.isNeighborFound && current.type == Type.RS_UNARY_OPERATOR){
                //change RSO to BO if possible and try to find neighbour again
                String currentToken = current.token;
                if(binOpNames.stream().anyMatch(bo -> bo.equals(currentToken))){
                    current = new Token(Type.BINARY_OPERATOR,currentToken);
                    result = findNeighbour(possibleNeighbours, neighboursAllowedTypes.get(current.type));
                }
            }

            if(!result.isNeighborFound) {
                throw new NoSuitableNeighborException(exp, current.token, possibleNeighbours.get(0).token);
            }

            output.add(current);
            current = result.suitNeighbor;
        }

        output.remove(0);
        return output;

    }

    NeighbourSeekingResult findNeighbour(List<Token> possibleNeighbors, List<Type> neighborsAllowedTypes){

        Optional<Token> neighbor = possibleNeighbors.stream()
                                          .filter(t -> neighborsAllowedTypes.contains(t.type))
                                          .findFirst();
        if(neighbor.isPresent())
            return new NeighbourSeekingResult(true,neighbor.get());

        return new NeighbourSeekingResult(false,null);
    }

    private class NeighbourSeekingResult {

        public final boolean isNeighborFound;
        public final Token suitNeighbor;

        public NeighbourSeekingResult(boolean isNeighborFound, Token suitNeighbor) {
            this.isNeighborFound = isNeighborFound;
            this.suitNeighbor = suitNeighbor;
        }
    }

}