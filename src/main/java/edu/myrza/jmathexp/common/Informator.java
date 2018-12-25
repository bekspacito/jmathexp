package edu.myrza.jmathexp.common;

import java.util.Set;

/**
 * Contains additional information of Operators and Functions
 * that is represented by Token.
 *
 * */
public interface Informator{

    /**
     * Returns an argument amount expected by given function
     * throws IllegalArgumentException if argument represents not function or
     * such function doesn't exist
     * */
    int funcArgc(Token function);

    /**
     * Returns true if operator is left associated , false if otherwise
     * throws IllegalArgumentException if argument represents not operator or
     * such operator doesn't exist
     * */
    boolean isLeftAssociated(Token operator);

    /**
     * Returns priority of operator
     * throws IllegalArgumentException if argument represents not operator or
     * such operator doesn't exist
     * */
    int priority(Token operator);


    /**
     * Returns a set of lexemesOf for a given lexeme type
     * e.g. for lexemesOf(Token.Type.BINARY_OPERATOR), the method will return all
     * the binary operator lexemes
     * */
    Set<String> lexemesOf(Token.Type type);

}