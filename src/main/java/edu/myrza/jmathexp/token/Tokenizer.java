package edu.myrza.jmathexp.token;

import java.util.*;
import static java.util.Comparator.*;
import static edu.myrza.jmathexp.token.Token.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;


public class Tokenizer{

    private Map<Token.Type,Set<String>> instanceNamesOfType;
    private static Map<Token.Type,List<Token.Type>> nextTokenAllowedTypes;

    static {
        nextTokenAllowedTypes = new HashMap<>();
        nextTokenAllowedTypes.put(Type.START,                    asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        nextTokenAllowedTypes.put(Type.LS_UNARY_OPERATOR,        asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        nextTokenAllowedTypes.put(Type.BINARY_OPERATOR,          asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        nextTokenAllowedTypes.put(Type.OPEN_PARENTHESES,         asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        nextTokenAllowedTypes.put(Type.FUNCTION_ARG_SEPARATOR,   asList(Type.OPERAND,Type.LS_UNARY_OPERATOR,Type.FUNCTION,Type.OPEN_PARENTHESES));
        nextTokenAllowedTypes.put(Type.OPERAND,                  asList(Type.RS_UNARY_OPERATOR,Type.BINARY_OPERATOR,Type.CLOSE_PARENTHESES,Type.FUNCTION_ARG_SEPARATOR,Type.END));
        nextTokenAllowedTypes.put(Type.RS_UNARY_OPERATOR,        asList(Type.RS_UNARY_OPERATOR,Type.BINARY_OPERATOR,Type.CLOSE_PARENTHESES,Type.FUNCTION_ARG_SEPARATOR,Type.END));
        nextTokenAllowedTypes.put(Type.CLOSE_PARENTHESES,        asList(Type.RS_UNARY_OPERATOR,Type.BINARY_OPERATOR,Type.CLOSE_PARENTHESES,Type.FUNCTION_ARG_SEPARATOR,Type.END));
        nextTokenAllowedTypes.put(Type.FUNCTION,                 asList(Type.OPEN_PARENTHESES));
    }

    public Tokenizer(Set<String> functionNames,
                     Set<String> binOpName,
                     Set<String> rightSideUnOpNames,
                     Set<String> leftSideUnOpNames)
    {
        if (functionNames == null || binOpName == null || rightSideUnOpNames == null || leftSideUnOpNames == null)
            throw new IllegalArgumentException("tokenizer cannot have null sets....");

            instanceNamesOfType = new HashMap<>();
            instanceNamesOfType.put(Type.FUNCTION,functionNames);
            instanceNamesOfType.put(Type.BINARY_OPERATOR,binOpName);
            instanceNamesOfType.put(Type.RS_UNARY_OPERATOR,rightSideUnOpNames);
            instanceNamesOfType.put(Type.LS_UNARY_OPERATOR,leftSideUnOpNames);
            instanceNamesOfType.put(Type.END,asList("]").stream().collect(toSet()));
            instanceNamesOfType.put(Type.CLOSE_PARENTHESES,asList(")").stream().collect(toSet()));
            instanceNamesOfType.put(Type.OPEN_PARENTHESES,asList("(").stream().collect(toSet()));
            instanceNamesOfType.put(Type.FUNCTION_ARG_SEPARATOR,asList(",").stream().collect(toSet()));

    }

    //todo then check here
    public List<Token> tokenize(String exp){

        if(exp == null || exp.isEmpty()) throw new IllegalArgumentException("WTF man?? Get that nasty-ass shit out of my face!!!!!...");

        exp = exp.trim().replaceAll("\\s+","");

        List<Token> result = new ArrayList<>();

        tokenize(result,exp);

        return result;
    }

    //todo then here
    private void tokenize(List<Token> result,String exp){

        int p = 0; //the amount of processed chars in the exp
        Token prev = new Token(Type.START,"[");
        Token next;
        Scanner in = new Scanner(exp + "]");
        in.useLocale(Locale.US);
        boolean isNextFound;

        //tokenizer code
        while (p <= exp.length()){

            isNextFound = false;

            List<Token.Type> nextTokenTypes = nextTokenAllowedTypes.get(prev.type);

            for(Token.Type t : nextTokenTypes){
                if(t.equals(Type.OPERAND)){
                    if(Character.isDigit(exp.charAt(p))){
                         String res = in.findInLine("([0-9]+(\\.[0-9]+)?|\\.[0-9]+)");
                         p += res.length();
                         next = new Token(t, res);
                         isNextFound = true;
                         result.add(prev);
                         prev = next;
                         break;
                    }
                    else continue;
                }

                Optional<String> optRes = instanceNamesOfType.get(t).stream()
                                                                    .sorted(comparingInt(String::length).reversed())
                                                                    .filter(n -> in.findWithinHorizon("\\Q" + n + "\\E",n.length()) != null)
                                                                    .findAny();

                if(optRes.isPresent()){
                    p += optRes.get().length();
                    next = new Token(t,optRes.get());
                    isNextFound = true;
                    result.add(prev);
                    prev = next;
                    break;
                }

            }

            String prevToken = prev.token;

            if(!isNextFound
               && prev.type.equals(Token.Type.RS_UNARY_OPERATOR)
               && instanceNamesOfType.get(Token.Type.BINARY_OPERATOR).stream().anyMatch(n -> n.equals(prevToken)))
            {
                prev = new Token(Type.BINARY_OPERATOR, prevToken);
            }else if(!isNextFound){
                //error
                System.out.println("an error occured...");
                System.exit(1);
            }

        }

        result.remove(0);
        in.close();
    }


}