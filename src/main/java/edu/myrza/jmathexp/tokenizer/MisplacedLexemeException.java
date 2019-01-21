package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;

public class MisplacedLexemeException extends RuntimeException {

    private int position;
    private String exp;
    private Token tokenAtWrongPlace;
    private String msg;


    public MisplacedLexemeException(int position, Token tokenAtWrongPlace, String exp){
        this.position = position;
        this.exp = exp;
        this.tokenAtWrongPlace = tokenAtWrongPlace;

        msg = "\nIn : " + exp + "\n" +
              "at position : " + position + "\n" +
              "wrong token " + tokenAtWrongPlace;
    }

    public int getPosition() {
        return position;
    }

    public String getExp() {
        return exp;
    }

    public Token getTokenAtWrongPlace(){
        return tokenAtWrongPlace;
    }

    public String getMessage(){
        return msg;
    }
}
