package edu.myrza.jmathexp.common;

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
            VARIABLE,
            OPEN_PARENTHESES,
            CLOSE_PARENTHESES;

        @Override
        public String toString() {

            switch (this){
                case START :                    return  "START";
                case END   :                    return  "END";
                case RS_UNARY_OPERATOR:         return "RS_UNARY_OPERATOR";
                case LS_UNARY_OPERATOR:         return "LS_UNARY_OPERATOR";
                case FUNCTION:                  return "FUNCTION";
                case FUNCTION_ARG_SEPARATOR:    return "FUNCTION_ARG_SEP";
                case BINARY_OPERATOR:           return "BINARY_OPERATOR";
                case OPERAND:                   return "OPERAND";
                case VARIABLE:                  return "VARIABLE";
                case OPEN_PARENTHESES:          return "OPEN_PARENTHESES";
                case CLOSE_PARENTHESES:         return "CLOSE_PARENTHESES";
                default :                       return null;
            }

        }

    }

    public final Type type;
    public final String lexeme;

    public Token(Type type,String lexeme){

        if(type == null || lexeme == null) throw new IllegalArgumentException("Token args cannot be null...");
        if(lexeme.isEmpty())               throw new IllegalArgumentException("There are no such tokenizer as empty string...");

        this.lexeme = lexeme;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", tokenizer='" + lexeme + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (type != token.type) return false;
        return lexeme.equals(token.lexeme);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + lexeme.hashCode();
        return result;
    }
}