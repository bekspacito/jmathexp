package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;

public class WrongFunArgException extends RuntimeException{

    private int funcPosition;
    private Token function;
    private String msg;
    private String exp;

    public WrongFunArgException(int funcPosition,Token function,String exp){
        this.funcPosition = funcPosition;
        this.function = function;
        this.exp = exp;

        msg = "\nWrong amount of arguments have been set to a function : " + function.lexeme +
              "\nat position : " + funcPosition;
    }

    public String getMessage(){
        return msg;
    }

    public int getFuncPosition() {
        return funcPosition;
    }

    public Token getFunction() {
        return function;
    }

    public String getExp(){
        return exp;
    }
}
