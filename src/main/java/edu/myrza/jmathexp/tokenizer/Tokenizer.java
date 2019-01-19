package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.*;

import static edu.myrza.jmathexp.common.Token.*;


public class Tokenizer{

    private final Set<String> variables;
    private final Informator informator;


    public Tokenizer(Informator informator,Set<String> variables){

        this.variables = variables;
        this.informator = informator;

    }

    public List<Token> tokenize(String exp){

        SyntaxAnalizer sAnalizer = new SyntaxAnalizer(exp,informator,variables);

        List<Token> output = new ArrayList<>();
        while (sAnalizer.hasNext())
            output.add(sAnalizer.next());

        sAnalizer.close();
        return output;

    }

}