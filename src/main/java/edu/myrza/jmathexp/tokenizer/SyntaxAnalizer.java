package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.*;

class SyntaxAnalizer{

    private List<Token> processedTokens;
    private Stack<Integer> parenthesesStack;
    private boolean ignoreNextOpenParentheses;
    private NeighborsMatcher neighborsMatcher;
    private Informator informator;
    private String exp;


    public SyntaxAnalizer(String exp,Informator informator,Set<String> variables){

        this.neighborsMatcher = new NeighborsMatcher(exp,informator,variables);
        this.informator = informator;
        this.exp = exp;

        parenthesesStack = new Stack<>();
        processedTokens = new ArrayList<>();
    }

    public boolean hasNext(){
        return neighborsMatcher.hasNext();
    }

    public Token next(){

        if(!neighborsMatcher.hasNext())
            throw new NoSuchElementException("no tokens left....");

        Token nextToken = neighborsMatcher.next();

        switch (nextToken.type){
            case FUNCTION :               {checkFunction(nextToken);        break;}
            case OPEN_PARENTHESES :       {checkOpenParentheses(nextToken); break;}
            case CLOSE_PARENTHESES :      {checkCloseParentheses(nextToken);break;}
            case FUNCTION_ARG_SEPARATOR : {checkArgSep(nextToken);          break;}
            case LS_UNARY_OPERATOR:       {checkLSOperator(nextToken);      break;}
            case RS_UNARY_OPERATOR:       {checkRSOperator(nextToken);      break;}
            default : break;
        }

        processedTokens.add(nextToken);
        return nextToken;
    }

    private void checkRSOperator(Token token){

        Token lastProcessedToken = processedTokens.get(processedTokens.size() - 1);

        if(lastProcessedToken.type != Token.Type.RS_UNARY_OPERATOR) return;
        if(informator.priority(lastProcessedToken) < informator.priority(token))
            throw new RuntimeException("Priority of " + token.lexeme + " is bigger than priority of " + lastProcessedToken.lexeme +
                                        ",so "  + token.lexeme + " cannot be after " + lastProcessedToken.lexeme);

    }

    private void checkLSOperator(Token token){

        if(processedTokens.size() <= 0) return;

        Token lastProcessedToken = processedTokens.get(processedTokens.size() - 1);

        if(lastProcessedToken.type != Token.Type.LS_UNARY_OPERATOR) return;
        if(informator.priority(lastProcessedToken) > informator.priority(token))
            throw new RuntimeException("Priority of " + token.lexeme + " is smaller than priority of " + lastProcessedToken.lexeme +
                    ",so "  + token.lexeme + " cannot be before " + lastProcessedToken.lexeme);

    }

    private void  checkFunction(Token function){

        int argc = informator.funcArgc(function);
        parenthesesStack.push(argc > 0 ? argc - 1 : 0);
        ignoreNextOpenParentheses = true;
    }

    private void checkOpenParentheses(Token openParentheses){

        if(ignoreNextOpenParentheses) {
            ignoreNextOpenParentheses = false;
            return;
        }
        parenthesesStack.push(0);

    }

    private void checkCloseParentheses(Token closeParentheses){

        if(parenthesesStack.empty())
            throw new RuntimeException("the amount of ) exceeded the amount of ( ...");

        int argc = parenthesesStack.pop();

        if(argc != 0)
            throwWrongAmountOfArgsException();
    }

    private void checkArgSep(Token argumentSep){
        if(parenthesesStack.empty()){
           throw new RuntimeException("arg sep isn't supposed to be here....");
        }//todo change exception!!!
        parenthesesStack.push(parenthesesStack.pop() - 1);
    }

    private void throwWrongAmountOfArgsException(){

        List<Token> temp = new ArrayList<>(processedTokens);
        int lastFunctionIndex = 0;

        Collections.reverse(temp);
        Token lastFunction = temp.stream().filter(t -> t.type == Token.Type.FUNCTION)
                                          .findFirst()
                                          .get();

        for(Token t : processedTokens)
            if(!t.equals(lastFunction)) lastFunctionIndex += t.lexeme.length();
            else break;

        String message = "\nWrong amount of arguments have been set to a function " + lastFunction.lexeme + " at position " + ++lastFunctionIndex;
        throw new RuntimeException(message);
    }

}