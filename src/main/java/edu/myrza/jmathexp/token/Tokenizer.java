package edu.myrza.jmathexp.token;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.*;
import static edu.myrza.jmathexp.token.Token.*;
import static java.util.Arrays.*;

//todo change Tokenizer constructor so that it receives

public class Tokenizer{

    private Map<Token.Type,Set<String>> instanceNamesOfType;
    private Map<Token.Type,List<NextTokenEvaluator>> nextTokenEvaluators;

    public Tokenizer(Set<String> functionNames,
                     Set<String> binOpName,
                     Set<String> rightSideUnOpNames,
                     Set<String> leftSideUnOpNames)
    {
        if (functionNames == null || binOpName == null || rightSideUnOpNames == null || leftSideUnOpNames == null)
            throw new IllegalArgumentException("tokenizer cannot have null sets....");

            instanceNamesOfType = new HashMap<>();
            instanceNamesOfType.put(Type.FUNCTION,functionNames);
            instanceNamesOfType.put(Type.BINARY_OPERATOR,binOpName);
            instanceNamesOfType.put(Type.RS_UNARY_OPERATOR,rightSideUnOpNames);
            instanceNamesOfType.put(Type.LS_UNARY_OPERATOR,leftSideUnOpNames);

            NextTokenEvaluator number = this::evaluateNumber;
            NextTokenEvaluator rso    = this::evaluateRSO;
            NextTokenEvaluator lso    = this::evaluateLSO;
            NextTokenEvaluator bo     = this::evaluateBO;
            NextTokenEvaluator func   = this::evaluateFunction;
            NextTokenEvaluator openP  = this::evaluateOpenParentheses;
            NextTokenEvaluator closeP = this::evaluateCloseParentheses;
            NextTokenEvaluator sep    = this::evaluateSeparator;
            NextTokenEvaluator end    = this::evaluateEnd;

            nextTokenEvaluators = new HashMap<>();

            nextTokenEvaluators.put(Type.START,                    asList(number,lso,func,openP));
            nextTokenEvaluators.put(Type.LS_UNARY_OPERATOR,        asList(number,lso,func,openP));
            nextTokenEvaluators.put(Type.BINARY_OPERATOR,          asList(number,lso,func,openP));
            nextTokenEvaluators.put(Type.OPEN_PARENTHESES,         asList(number,lso,func,openP));
            nextTokenEvaluators.put(Type.FUNCTION_ARG_SEPARATOR,   asList(number,lso,func,openP));
            nextTokenEvaluators.put(Type.OPERAND,                  asList(rso,bo,closeP,sep,end));
            nextTokenEvaluators.put(Type.RS_UNARY_OPERATOR,        asList(rso,bo,closeP,sep,end));
            nextTokenEvaluators.put(Type.CLOSE_PARENTHESES,        asList(rso,bo,closeP,sep,end));
            nextTokenEvaluators.put(Type.FUNCTION,                 asList(openP));

    }

    //todo then check here
    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty()) throw new IllegalArgumentException("WTF man?? Get that nasty-ass shit out of my face!!!!!...");

        exp = exp.trim().replaceAll("\\s+","");

        List<Token> result = tokenizeOne(exp);

        return result;
    }

    //todo then here
    private List<Token> tokenizeOne(String exp){

        Integer p = 0; //the amount of processed chars in the exp
        Token prev = new Token(Type.START,"[");
        Token next;
        Scanner in = new Scanner(exp + "]");
        in.useLocale(Locale.US);
        List<Token> result = new ArrayList<>();
        boolean isNextFound;

        //tokenize code
        while (p <= exp.length()){

            final int finalP = p;
            Optional<NextTokenEvaluationResult> optRes = nextTokenEvaluators.get(prev.type).stream()
                                                                                           .map(nextTokenEvaluator -> nextTokenEvaluator.ev(in,exp,finalP))
                                                                                           .filter(nteResult -> nteResult.isSuccessful)
                                                                                           .findFirst();

            if(optRes.isPresent() && optRes.get().isSuccessful){
                p = optRes.get().newPointer;
                result.add(prev);
                prev = optRes.get().evaluatedToken;
                continue;
            }

            String prevToken = prev.token;

            if(prev.type.equals(Token.Type.RS_UNARY_OPERATOR)
               && instanceNamesOfType.get(Token.Type.BINARY_OPERATOR).stream().anyMatch(n -> n.equals(prevToken)))
            {
                prev = new Token(Type.BINARY_OPERATOR, prevToken);
                continue;
            }

            //todo handle errors here

        }

        result.remove(0);
        in.close();


        return result;

    }

    private NextTokenEvaluationResult evaluateNumber(Scanner in, String exp, int pointer){
        if(Character.isDigit(exp.charAt(pointer))){
            String res = in.findInLine("([0-9]+(\\.[0-9]+)?|\\.[0-9]+)");
            pointer += res.length();
            return new NextTokenEvaluationResult(true,new Token(Type.OPERAND,res),pointer);
        }
        return new NextTokenEvaluationResult(false,null,pointer);
    }

    private NextTokenEvaluationResult evaluateLSO(Scanner in,String exp,int pointer){
        return evaluateNextTokenOf(Type.LS_UNARY_OPERATOR,in,exp,pointer);
    }

    private NextTokenEvaluationResult evaluateRSO(Scanner in,String exp,int pointer){
        return evaluateNextTokenOf(Type.RS_UNARY_OPERATOR,in,exp,pointer);
    }

    private NextTokenEvaluationResult evaluateBO(Scanner in,String exp,int pointer){
        return evaluateNextTokenOf(Type.BINARY_OPERATOR,in,exp,pointer);
    }

    private NextTokenEvaluationResult evaluateFunction(Scanner in,String exp,int pointer){
        return evaluateNextTokenOf(Type.FUNCTION,in,exp,pointer);
    }

    private NextTokenEvaluationResult evaluateOpenParentheses(Scanner in,String exp,int pointer){
        return evaluateReversedSymbol(in,pointer,"(",Type.OPEN_PARENTHESES);
    }

    private NextTokenEvaluationResult evaluateCloseParentheses(Scanner in,String exp,int pointer){
        return evaluateReversedSymbol(in,pointer,")",Type.CLOSE_PARENTHESES);
    }

    private NextTokenEvaluationResult evaluateSeparator(Scanner in,String exp,int pointer){
        return evaluateReversedSymbol(in,pointer,",",Type.FUNCTION_ARG_SEPARATOR);
    }

    private NextTokenEvaluationResult evaluateEnd(Scanner in,String exp,int pointer){
        return evaluateReversedSymbol(in,pointer,"]",Type.END);
    }

    private NextTokenEvaluationResult evaluateReversedSymbol(Scanner in,int pointer,String symbol,Token.Type type){
        if(in.findWithinHorizon("\\Q" + symbol + "\\E",1) != null){
            return new NextTokenEvaluationResult(true,new Token(type,symbol),pointer + 1);
        }
        return new NextTokenEvaluationResult(false,null,pointer);
    }

    private NextTokenEvaluationResult evaluateNextTokenOf(Token.Type t,Scanner in,String exp,int pointer){

        Optional<String> optRes = instanceNamesOfType.get(t).stream()
                                                            .sorted(comparingInt(String::length).reversed())//todo remove this sorting operation
                                                            .filter(n -> in.findWithinHorizon("\\Q" + n + "\\E",n.length()) != null)
                                                            .findFirst();
        if(optRes.isPresent()){
            pointer += optRes.get().length();
            return new NextTokenEvaluationResult(true,new Token(t,optRes.get()),pointer);
        }
        return new NextTokenEvaluationResult(false,null,pointer);

    }

    private interface NextTokenEvaluator{

        NextTokenEvaluationResult ev(Scanner in,String exp,int pointer);

    }

    private class NextTokenEvaluationResult{

        public final boolean isSuccessful;
        public final Token evaluatedToken;
        public final int newPointer;

        public NextTokenEvaluationResult(boolean isSuccessful, Token evaluatedToken,int newPointer) {
            this.isSuccessful = isSuccessful;
            this.evaluatedToken = evaluatedToken;
            this.newPointer = newPointer;
        }
    }

}