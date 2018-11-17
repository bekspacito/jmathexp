package edu.myrza.jmathexp.tokenizer;

import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;

import java.util.*;

import static edu.myrza.jmathexp.common.Token.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.toSet;

//todo Handle errors
//todo change Tokenizer constructor so that it receives an Informator
//todo function names have to be sorted decreasingly by length
//todo RSO names have to be sorted decreasingly by priority of RSO operations
//todo LSO operations have to be sorted increasingly by priority of LSO operations

public class Tokenizer{

    private final Set<String> functions;
    private final Set<String> binOpNames;
    private final Set<String> rsOpNames;
    private final Set<String> lsOpNames;

    public Tokenizer(ExpressionUnitFactory factory){

        functions = factory.getIds(ExpUnitType.FUNCTION);
        binOpNames = factory.getIds(ExpUnitType.BINARY_OPERATOR);

        rsOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                .filter(eu -> factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                .collect(toSet());

        lsOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                .filter(eu -> !factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                .collect(toSet());

    }

    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty())
            throw new IllegalArgumentException("the math expression can neither be null nor be empty string...");

        LexicalAnalizer lex = new LexicalAnalizer(exp,functions,rsOpNames,lsOpNames,binOpNames);
        NeighborsMatcher nm = new NeighborsMatcher(exp,binOpNames,lex);

        List<Token> output = new ArrayList<>();
        while (nm.hasNext())
            output.add(nm.next());

        output.remove(output.size()-1);
        return output;

    }

}