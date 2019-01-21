package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class NoSuitableNeighborsException extends RuntimeException{

    private String      msg;
    private Token       neighbor1;
    private Token       neighbor2;
    private List<Token> processedTokens; //list of tokens processed before the exception happen
    private String      exp;
    private int         errorOccurPos;

    public NoSuitableNeighborsException(Token neighbor1,
                                        Token neighbor2,
                                        List<Token> processedTokens,
                                        String exp)
    {
        this.neighbor1 = neighbor1;
        this.neighbor2 = neighbor2;
        this.processedTokens = processedTokens;
        this.exp = exp;
        this.errorOccurPos = processedTokens.stream()
                                            .mapToInt(t -> t.lexeme.length())
                                            .sum();

        if(neighbor1.type == Token.Type.START) {
            msg = "Token [" + neighbor2.lexeme + "] cannot be at the start....";
        }
        else if(neighbor2.type == Token.Type.END) {
            msg = "The expression [" + exp + "] is unfinished...";
        }
        else
            msg = formErrorMessage(neighbor1,neighbor2,processedTokens);

    }

    private String formErrorMessage(Token current,Token badNeighbor,List<Token> processedTokens)
    {
        String tokenStr = processedTokens.stream().map(t -> "[" + t.lexeme + "]").collect(joining());
        int errorOccurencePosition = tokenStr.length() - processedTokens.size()*2;

        return  "\nThese two tokens : [" + current.lexeme + "][" + badNeighbor.lexeme + "] cannot be neighbors....\n" +
                "In expression : " + exp + "\n" +
                "At position   : " + errorOccurencePosition + "\n";
    }


    public String getMessage() {
        return msg;
    }

    public int getPositionInExp(){
        return errorOccurPos - 1;
    }

    public Token getNeighbor1() {
        return neighbor1;
    }

    public Token getNeighbor2() {
        return neighbor2;
    }

    public List<Token> getProcessedTokens() {
        return processedTokens;
    }

    public String getExp() {
        return exp;
    }
}
