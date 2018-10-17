package edu.myrza.jmathexp.token;

import edu.myrza.jmathexp.expression_unit.ExpressionUnit;

public class Token{

    public enum Type{
            UNARY_OPERATOR,
            BINARY_OPERATOR,
            FUNCTION,
            FUNCTION_ARG_SEPARATOR,
            OPERAND,
            OPEN_PARENTHESES,
            CLOSE_PARENTHESES
        }

    public final Type type;
    public final String token;

    public Token(Type type,String token){

        if(type == null || token == null) throw new IllegalArgumentException("Token args cannot be null...");
        if(token.isEmpty())               throw new IllegalArgumentException("There are no such token as empty string...");

        this.token = token;
        this.type = type;
    }

}