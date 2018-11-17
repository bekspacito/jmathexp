package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;

import java.util.*;
import static java.util.stream.Collectors.*;

import static java.util.Arrays.asList;

class NeighborsMatcher{

    private LexicalAnalizer lex;
    private List<Token> output;
    private Set<String> binOpNames;
    private Token current = new Token(Token.Type.START,"[");
    private static Map<Token.Type,List<Token.Type>> neighboursAllowedTypes = new HashMap<>();

    public NeighborsMatcher(String exp,Set<String> binOpNames,LexicalAnalizer lex){
        this.lex = lex;
        this.binOpNames = binOpNames;
        output = new ArrayList<>();
    }

    static {

        List<Token.Type> allowTypesSetOne = asList(Token.Type.OPERAND, Token.Type.LS_UNARY_OPERATOR, Token.Type.FUNCTION, Token.Type.OPEN_PARENTHESES);
        List<Token.Type> allowTypesSetTwo = asList(Token.Type.RS_UNARY_OPERATOR, Token.Type.BINARY_OPERATOR, Token.Type.CLOSE_PARENTHESES, Token.Type.FUNCTION_ARG_SEPARATOR, Token.Type.END);

        neighboursAllowedTypes.put(Token.Type.START,                  allowTypesSetOne);
        neighboursAllowedTypes.put(Token.Type.LS_UNARY_OPERATOR,      allowTypesSetOne);
        neighboursAllowedTypes.put(Token.Type.BINARY_OPERATOR,        allowTypesSetOne);
        neighboursAllowedTypes.put(Token.Type.OPEN_PARENTHESES,       allowTypesSetOne);
        neighboursAllowedTypes.put(Token.Type.FUNCTION_ARG_SEPARATOR, allowTypesSetOne);

        neighboursAllowedTypes.put(Token.Type.OPERAND,                allowTypesSetTwo);
        neighboursAllowedTypes.put(Token.Type.RS_UNARY_OPERATOR,      allowTypesSetTwo);
        neighboursAllowedTypes.put(Token.Type.CLOSE_PARENTHESES,      allowTypesSetTwo);

        neighboursAllowedTypes.put(Token.Type.FUNCTION,               asList(Token.Type.OPEN_PARENTHESES));

    }

    public boolean hasNext(){ return lex.hasNext(); }

    public Token next(){

        List<Token> possibleNeighbours = null;
        NeighbourSeekingResult result = null;

        if (!lex.hasNext())
            throw new NoSuchElementException("no tokens left....");

        possibleNeighbours = lex.next();
        result = findNeighbour(possibleNeighbours, neighboursAllowedTypes.get(current.type));

        if(!result.isNeighborFound && current.type == Token.Type.RS_UNARY_OPERATOR){
            //change RSO to BO if possible and try to find neighbour again
            String currentToken = current.token;
            if(binOpNames.stream().anyMatch(bo -> bo.equals(currentToken))){
                current = new Token(Token.Type.BINARY_OPERATOR,currentToken);
                result = findNeighbour(possibleNeighbours, neighboursAllowedTypes.get(current.type));
            }
        }

        if(!result.isNeighborFound) {
            throw new NoSuitableNeighborException(output.stream().map(t -> "[" + t.token + "]").collect(joining()), current.token, possibleNeighbours.get(0).token);
        }

        output.add(current);
        current = result.suitNeighbor;
        return current;

    }

    public void close(){ }

    NeighbourSeekingResult findNeighbour(List<Token> possibleNeighbors, List<Token.Type> neighborsAllowedTypes){

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