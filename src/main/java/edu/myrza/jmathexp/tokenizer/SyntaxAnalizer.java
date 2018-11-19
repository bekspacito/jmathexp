package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class SyntaxAnalizer{

    private List<Token> processedTokens;
    private Stack<Integer> parenthesesStack;
    private boolean ignoreNextOpenParentheses;
    private NeighborsMatcher neighborsMatcher;
    private Informator informator;


    public SyntaxAnalizer(NeighborsMatcher neighborsMatcher, Informator informator){
        this.neighborsMatcher = neighborsMatcher;
        this.informator = informator;

        parenthesesStack = new Stack<>();
        processedTokens = new ArrayList<>();
    }

    public boolean hasNext(){
        return neighborsMatcher.hasNext();
    }

    //todo implement this!!!!!
    public Token next(){

        if(!neighborsMatcher.hasNext())
            throw new NoSuchElementException("no tokens left....");

        Token nextToken = neighborsMatcher.next();

        switch (nextToken.type){
            case FUNCTION : {checkFunction(nextToken); break;}
            case OPEN_PARENTHESES : {checkOpenParentheses(nextToken);break;}
            case CLOSE_PARENTHESES : {checkCloseParentheses(nextToken);break;}
            case FUNCTION_ARG_SEPARATOR : {checkArgSep(nextToken);break;}
            default : break;
        }

        return nextToken;
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

        if(argc > 0)
            throw new RuntimeException("not all of the last function's arguments have been put....");
        if(argc < 0)
            throw new RuntimeException("either arg. separator isn't supposed to be here or " +
                                        "too much arguments has been sent to the last function...");
    }

    private void checkArgSep(Token argumentSep){
        if(parenthesesStack.empty()){
           throw new RuntimeException("arg sep isn't supposed to be here....");
        }//todo change exception!!!
        parenthesesStack.push(parenthesesStack.pop() - 1);
    }

}