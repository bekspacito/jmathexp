package edu.myrza.jmathexp.token;

import edu.myrza.jmathexp.expression_unit.ExpressionUnit;

public class Token{

    public enum Type{
            START, // [ symbol that represents zero symbol and
            END,   // ] symbol that represents expression closing symbol  e.g. [-4+3!]
            RS_UNARY_OPERATOR,
            LS_UNARY_OPERATOR,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token1 = (Token) o;

        if (type != token1.type) return false;
        return token != null ? token.equals(token1.token) : token1.token == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}