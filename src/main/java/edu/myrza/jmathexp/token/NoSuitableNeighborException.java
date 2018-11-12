package edu.myrza.jmathexp.token;

public class NoSuitableNeighborException extends RuntimeException{
    public NoSuitableNeighborException(String exp,String a,String b){
        super("In [" + exp + "] " +
                "\nThese two tokens : [" + a + "][" + b + "] cannot be neighbors....");
    }
}
