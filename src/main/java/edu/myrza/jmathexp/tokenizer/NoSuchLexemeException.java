package edu.myrza.jmathexp.tokenizer;

public class NoSuchLexemeException extends RuntimeException {

    private int unkLexStartIndex;
    private String exp;

    public NoSuchLexemeException(String exp, int unkLexStartIndex){
        super("\nAn unknown lexeme appeared in [" + exp +
                "\nthe unknown lexeme starts at position : " + (unkLexStartIndex + 1));
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
