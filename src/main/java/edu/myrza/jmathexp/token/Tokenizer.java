package edu.myrza.jmathexp.token;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.*;
import static edu.myrza.jmathexp.token.Token.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.toList;
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

    public static class LexicalAnalizer{

        private Scanner scanner;
        private int pointer;
        private String exp;
        private List<String> functions;
        private Set<String> rsOperators;
        private Set<String> lsOperators;
        private Set<String> binaryOperators;

        private List<String> operators;
        private List<Token>  reserveSymbols;

        public LexicalAnalizer(String exp,Set<String> functions,
                                           Set<String> rsOperators,
                                           Set<String> lsOperators,
                                           Set<String> binaryOperators)
        {

            this.exp = exp.replaceAll("\\s+","") + "]";
            scanner = new Scanner(this.exp);
            pointer = 0;

            this.binaryOperators = binaryOperators;
            this.rsOperators = rsOperators;
            this.lsOperators = lsOperators;

            //we sorted an operators and functions by length scanner order to apply LONGEST MATCHING RULE
            this.functions = functions.stream()
                                      .sorted(comparingInt(String::length).reversed())
                                      .collect(toList());

             operators = Stream.of(rsOperators,lsOperators,binaryOperators)
                              .flatMap(Set::stream)
                              .distinct()
                              .sorted(comparingInt(String::length).reversed())
                              .collect(toList());



             reserveSymbols = asList(new Token(Type.OPEN_PARENTHESES,"("),
                                     new Token(Type.CLOSE_PARENTHESES,")"),
                                     new Token(Type.FUNCTION_ARG_SEPARATOR,","),
                                     new Token(Type.END,"]"));



        }

        public boolean hasNext(){ return pointer < exp.length(); }

        public List<Token> next(){

            if(pointer >= exp.length())
                throw new NoSuchElementException("no tokens left....");

            List<Token> result = new ArrayList<>();
            String nextTokenStr = null;

            //is operand
            if(Character.isDigit(exp.charAt(pointer))) {
                nextTokenStr = scanner.findInLine("([0-9]+(\\.[0-9]+)?|\\.[0-9]+)");
                pointer += nextTokenStr.length();
                result.add(new Token(Type.OPERAND,nextTokenStr));
                return result;
            }

            //is findFunction todo or variable
            if(Character.isLetter(exp.charAt(pointer))) {
                nextTokenStr = findFunction().get();
                pointer += nextTokenStr.length();
                result.add(new Token(Type.FUNCTION,nextTokenStr));
                return result;
            }

            //is findOperator
            Optional<String> res = findOperator();
            if(res.isPresent()) {
                pointer += res.get().length();
                return getMatchedTokens(res.get());
            }


            //is reversed symbols
            Optional<Token> resToken = reserveSymbols.stream()
                    .filter(t -> scanner.findWithinHorizon("\\Q" + t.token + "\\E",t.token.length()) != null)
                    .findFirst();

            if(resToken.isPresent()){
                pointer += resToken.get().token.length();
                result.add(resToken.get());
                return result;
            }

            throw new NoSuchTokenException(exp,pointer);
        }

        private List<Token> getMatchedTokens(String nextTokenStr){

            List<Token> result = new ArrayList<>();

            if(rsOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Type.RS_UNARY_OPERATOR,nextTokenStr));
            if(lsOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Type.LS_UNARY_OPERATOR,nextTokenStr));
            if(binaryOperators.stream().anyMatch(str -> str.equals(nextTokenStr)))
                result.add(new Token(Type.BINARY_OPERATOR,nextTokenStr));

            return result;
        }

        private Optional<String> findOperator(){
            return operators.stream()
                            .filter(str -> scanner.findWithinHorizon("\\Q" + str + "\\E",str.length()) != null)
                            .findFirst();
        }

        private Optional<String> findFunction(){
            return functions.stream()
                            .filter(str -> scanner.findWithinHorizon("\\Q" + str + "\\E",str.length()) != null)
                            .findFirst();

        }

        public void finalize(){
            scanner.close();
        }

    }

    private interface NeighborSeeker {
        NeighbourSeekingResult find(List<Token> possibleNeighbors, List<Type> neighborsAllowedTypes);
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