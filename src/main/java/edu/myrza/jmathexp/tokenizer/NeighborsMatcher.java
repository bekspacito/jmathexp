package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;

class NeighborsMatcher{

    private LexicalAnalizer lex;
    private String exp;
    private List<Token> output;
    private Set<String> binOpNames;
    private Token current;
    private static Map<Token.Type,List<Token.Type>> neighboursAllowedTypes = new HashMap<>();

    public NeighborsMatcher(String exp,Set<String> binOpNames,LexicalAnalizer lex){
        this.lex = lex;
        this.binOpNames = binOpNames;
        this.exp = exp;
        this.current = new Token(Token.Type.START,"[");
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
            String currentToken = current.lexeme;
            if(binOpNames.stream().anyMatch(bo -> bo.equals(currentToken))){
                current = new Token(Token.Type.BINARY_OPERATOR,currentToken);
                result = findNeighbour(possibleNeighbours, neighboursAllowedTypes.get(current.type));
            }
        }

        if(!result.isNeighborFound) {
            handleNoSuitableNeighbors(current,possibleNeighbours.get(0));
        }

        output.add(current);
        current = result.suitNeighbor;
        return current;

    }

    NeighbourSeekingResult findNeighbour(List<Token> possibleNeighbors, List<Token.Type> neighborsAllowedTypes){

        Optional<Token> neighbor = possibleNeighbors.stream()
                                                    .filter(t -> neighborsAllowedTypes.contains(t.type))
                                                    .findFirst();
        if(neighbor.isPresent())
            return new NeighbourSeekingResult(true,neighbor.get());

        return new NeighbourSeekingResult(false,null);
    }

    private void handleNoSuitableNeighbors(Token current , Token badNeighbor){

        if(current.type == Token.Type.START)
            throw new RuntimeException("Token [" + badNeighbor.lexeme + "] cannot be at the start....");

        if(badNeighbor.type == Token.Type.END)
            throw new RuntimeException("The expression [" + exp + "] is unfinished...");

        //todo handle TOKEN.Type.START being in output
        output.remove(0);
        String tokenStr = output.stream().map(t -> "[" + t.lexeme + "]").collect(joining());
        int errorOccurencePosition = tokenStr.length() - output.size()*2;

        String message = "At position " + errorOccurencePosition + "\n" +
                         "These two tokens : [" + current.lexeme + "][" + badNeighbor.lexeme + "] cannot be neighbors....\n" +
                         "prev. tokens : " + tokenStr;

        throw new RuntimeException(message);

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