package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.*;

import static edu.myrza.jmathexp.common.Token.*;


public class Tokenizer{

    private final Set<String> funcLexemes;
    private final Set<String> boLexemes;
    private final Set<String> rsoLexemes;
    private final Set<String> lsoLexemes;
    private final Set<String> variables;
    private final Informator informator;


    public Tokenizer(Informator informator,Set<String> variables){

        funcLexemes = informator.lexemesOf(Type.FUNCTION);
        boLexemes = informator.lexemesOf(Type.BINARY_OPERATOR);
        rsoLexemes = informator.lexemesOf(Type.RS_UNARY_OPERATOR);
        lsoLexemes = informator.lexemesOf(Type.LS_UNARY_OPERATOR);
        this.variables = variables;
        this.informator = informator;

    }

    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty())
            throw new IllegalArgumentException("the math expression can neither be null nor be empty string...");

        LexicalAnalizer lex = new LexicalAnalizer(exp,variables, funcLexemes, rsoLexemes, lsoLexemes, boLexemes);
        NeighborsMatcher matcher = new NeighborsMatcher(exp, lex);
        SyntaxAnalizer sAnalizer = new SyntaxAnalizer(exp,matcher,informator);

        List<Token> output = new ArrayList<>();
        while (sAnalizer.hasNext())
            output.add(sAnalizer.next());

        return output;

    }

}