package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.*;

import static edu.myrza.jmathexp.common.Token.*;


public class Tokenizer{

    private final Set<String> fLexemes;
    private final Set<String> boLexemes;
    private final Set<String> rsoLexemes;
    private final Set<String> lsoLexemes;
    private final Informator informator;

    public Tokenizer(Informator informator){

        fLexemes = informator.lexemesOf(Type.FUNCTION);
        boLexemes = informator.lexemesOf(Type.BINARY_OPERATOR);
        rsoLexemes = informator.lexemesOf(Type.RS_UNARY_OPERATOR);
        lsoLexemes = informator.lexemesOf(Type.LS_UNARY_OPERATOR);
        this.informator = informator;

    }

    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty())
            throw new IllegalArgumentException("the math expression can neither be null nor be empty string...");

        LexicalAnalizer lex = new LexicalAnalizer(exp, fLexemes, rsoLexemes, lsoLexemes, boLexemes);
        NeighborsMatcher nm = new NeighborsMatcher(exp, boLexemes,lex);
        SyntaxAnalizer sa = new SyntaxAnalizer(exp,nm,informator);

        List<Token> output = new ArrayList<>();
        while (sa.hasNext())
            output.add(sa.next());

        output.remove(output.size()-1);
        return output;

    }

}