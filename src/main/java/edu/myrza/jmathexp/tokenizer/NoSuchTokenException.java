package edu.myrza.jmathexp.tokenizer;

public class NoSuchTokenException extends RuntimeException {

    public NoSuchTokenException(String exp,int indexOfUnknownToken){
        super("An unknown tokenizer appeared in [" + exp + "]\nstarting at position : " + ++indexOfUnknownToken);
    }

}
