package edu.myrza.jmathexp.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Tokenizer{

    private Set<String> functionNames;
    private Set<String> binOpName;
    private Set<String> unOpNames;

    public Tokenizer(Set<String> functionNames,
                     Set<String> binOpName,
                     Set<String> unOpNames)
    {
        if (functionNames == null || binOpName == null || unOpNames == null)
            throw new IllegalArgumentException("tokenizer cannot have null sets....");

        this.binOpName = binOpName;
        this.functionNames = functionNames;
        this.unOpNames = unOpNames;

    }

    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty()) throw new IllegalArgumentException("WTF man?? Get that nasty-ass shit out of my face!!!!!...");

        List<Token> result = new ArrayList<>();
        Token prevToken = null;

        exp = exp.trim().replaceAll("\\s+","");




        return result;
    }

    private boolean isOperator(Token t){
         return t.type == Token.Type.BINARY_OPERATOR || t.type == Token.Type.UNARY_OPERATOR;
    }


}