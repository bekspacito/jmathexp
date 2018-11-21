package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;

import java.util.List;

public class NoSuitableNeighborException extends RuntimeException{
    public NoSuitableNeighborException(String exp, String a, String b, List<Token> prevProcessedTokens){
        super("In [" + exp + "] " +
                "\nThese two tokens : [" + a + "][" + b + "] cannot be neighbors....");

    }
}
