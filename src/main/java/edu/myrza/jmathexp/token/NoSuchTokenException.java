package edu.myrza.jmathexp.token;

public class NoSuchTokenException extends RuntimeException {

    public NoSuchTokenException(String exp,int indexOfUnknownToken){
        super("An unknown token appeared in [" + exp + "] at " + indexOfUnknownToken);
    }

}
