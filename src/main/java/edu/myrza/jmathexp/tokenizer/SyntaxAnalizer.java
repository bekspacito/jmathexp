package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.*;

class SyntaxAnalizer{

    private List<Token> processedTokens;
    private Stack<Integer> parenthesesStack; //contains how many arg. separators are expected
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
            throwMisplacedLexemeExcp(closeParentheses);

        int argc = parenthesesStack.pop();

        if(argc != 0)
            throwWrongAmountOfArgsException();
    }

    private void checkArgSep(Token argumentSep){
        if(parenthesesStack.empty() || parenthesesStack.peek() == 0)
            throwMisplacedLexemeExcp(argumentSep);

        parenthesesStack.push(parenthesesStack.pop() - 1);
    }

    private void throwWrongAmountOfArgsException(){

        int l1 = processedTokens.stream()
                                 .mapToInt(t -> t.lexeme.length())
                                 .sum();

        Collections.reverse(processedTokens);

        Token function = processedTokens.stream().filter(t -> t.type == Token.Type.FUNCTION)
                                        .findFirst()
                                        .get();

        int l2 = 0;
        for(Token t : processedTokens)
            if(!t.equals(function)) l2 += t.lexeme.length();
            else break;

        l2 = l2 + function.lexeme.length();

        throw new WrongFunArgException(l1 - l2,function,exp);
    }

    private void throwMisplacedLexemeExcp(Token mispToken){
        int position = processedTokens.stream()
                .mapToInt(t -> t.lexeme.length())
                .sum();

        throw new MisplacedLexemeException(position,mispToken,exp);
    }

    public void close(){
        neighborsMatcher.close();
    }

}