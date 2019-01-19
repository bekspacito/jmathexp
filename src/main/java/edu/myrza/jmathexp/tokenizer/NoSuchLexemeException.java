package edu.myrza.jmathexp.tokenizer;

public class NoSuchLexemeException extends RuntimeException {

    private int unkLexStartIndex;
    private String exp;

    public NoSuchLexemeException(String exp, int unkLexStartIndex){
        super("An unknown lexeme appeared in [" + exp + "]" +
                "\nthe lexeme starts at position : " + unkLexStartIndex);
        this.unkLexStartIndex = unkLexStartIndex;
        this.exp = exp;
    }

    public int getUnkLexStartIndex(){
        return unkLexStartIndex;
    }

    public String getExp() {
        return exp;
    }
}
