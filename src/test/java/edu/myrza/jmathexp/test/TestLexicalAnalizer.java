package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactoryImpl;
import edu.myrza.jmathexp.token.NoSuchTokenException;
import edu.myrza.jmathexp.token.Token;
import edu.myrza.jmathexp.token.Tokenizer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static edu.myrza.jmathexp.expression_unit.BuiltInOperators.POWER_PRECEDENCE;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class TestLexicalAnalizer {

    Set<String> functions;
    Set<String> binOpNames;
    Set<String> rsOpNames;
    Set<String> lsOpNames;

   public TestLexicalAnalizer(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                .addUnaryOperator("!",true,POWER_PRECEDENCE + 1,arg -> {
                    int intArg = (int)arg;
                    return IntStream.rangeClosed(1,intArg)
                            .reduce((i,j) -> i*j)
                            .getAsInt();
                })
                .addUnaryOperator("++",true,POWER_PRECEDENCE + 100,arg -> arg + 1)
                .build();

        functions = factory.getIds(ExpUnitType.FUNCTION);
        binOpNames    = factory.getIds(ExpUnitType.BINARY_OPERATOR);

        rsOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                .filter(eu -> factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                .collect(toSet());

        lsOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                .filter(eu -> !factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                .collect(toSet());
    }

    @Test
    public void test1(){

        String exp = "-3+4!";
        List<List<Token>> result = getAllTheTokens(new Tokenizer.LexicalAnalizer(exp,functions,rsOpNames,lsOpNames,binOpNames));

        assertTrue(result.get(0).size() == 2);
        assertTrue(contains(result.get(0),new Token(Token.Type.LS_UNARY_OPERATOR,"-"),new Token(Token.Type.BINARY_OPERATOR,"-")));

        assertTrue(result.get(1).size() == 1);
        assertTrue(contains(result.get(1),new Token(Token.Type.OPERAND,"3")));

        assertTrue(result.get(2).size() == 2);
        assertTrue(contains(result.get(2),new Token(Token.Type.LS_UNARY_OPERATOR,"+"),new Token(Token.Type.BINARY_OPERATOR,"+")));

        assertTrue(result.get(3).size() == 1);
        assertTrue(contains(result.get(3),new Token(Token.Type.OPERAND,"4")));

        assertTrue(result.get(4).size() == 1);
        assertTrue(contains(result.get(4),new Token(Token.Type.RS_UNARY_OPERATOR,"!")));

    }

    @Test
    public void test2(){

        String exp = "+--+2*4+-log(3)";
        List<List<Token>> result = getAllTheTokens(new Tokenizer.LexicalAnalizer(exp,functions,rsOpNames,lsOpNames,binOpNames));

        assertTrue(result.get(0).size() == 2);
        assertTrue(contains(result.get(0),new Token(Token.Type.LS_UNARY_OPERATOR,"+"),new Token(Token.Type.BINARY_OPERATOR,"+")));

        assertTrue(result.get(1).size() == 2);
        assertTrue(contains(result.get(1),new Token(Token.Type.LS_UNARY_OPERATOR,"-"),new Token(Token.Type.BINARY_OPERATOR,"-")));

        assertTrue(result.get(2).size() == 2);
        assertTrue(contains(result.get(2),new Token(Token.Type.LS_UNARY_OPERATOR,"-"),new Token(Token.Type.BINARY_OPERATOR,"-")));

        assertTrue(result.get(3).size() == 2);
        assertTrue(contains(result.get(3),new Token(Token.Type.LS_UNARY_OPERATOR,"+"),new Token(Token.Type.BINARY_OPERATOR,"+")));

        assertTrue(result.get(4).size() == 1);
        assertTrue(contains(result.get(4),new Token(Token.Type.OPERAND,"2")));

        assertTrue(result.get(5).size() == 1);
        assertTrue(contains(result.get(5),new Token(Token.Type.BINARY_OPERATOR,"*")));

        assertTrue(result.get(6).size() == 1);
        assertTrue(contains(result.get(6),new Token(Token.Type.OPERAND,"4")));

        assertTrue(result.get(7).size() == 2);
        assertTrue(contains(result.get(7),new Token(Token.Type.LS_UNARY_OPERATOR,"+"),new Token(Token.Type.BINARY_OPERATOR,"+")));

        assertTrue(result.get(8).size() == 2);
        assertTrue(contains(result.get(8),new Token(Token.Type.LS_UNARY_OPERATOR,"-"),new Token(Token.Type.BINARY_OPERATOR,"-")));

        assertTrue(result.get(9).size() == 1);
        assertTrue(contains(result.get(9),new Token(Token.Type.FUNCTION,"log")));

        assertTrue(contains(result.get(10),new Token(Token.Type.OPEN_PARENTHESES,"(")));

        assertTrue(result.get(11).size() == 1);
        assertTrue(contains(result.get(11),new Token(Token.Type.OPERAND,"3")));

        assertTrue(contains(result.get(12),new Token(Token.Type.CLOSE_PARENTHESES,")")));


    }


    @Test(expected = NoSuchTokenException.class)
    public void test3(){

        String exp = "2+3$$5"; // error $$ is an undefined operator
        List<List<Token>> result = getAllTheTokens(new Tokenizer.LexicalAnalizer(exp,functions,rsOpNames,lsOpNames,binOpNames));

    }

    //longest matching rule testing
    public void test4(){

        String exp = "3+++++4";
        List<List<Token>> result = getAllTheTokens(new Tokenizer.LexicalAnalizer(exp,functions,rsOpNames,lsOpNames,binOpNames));

        assertTrue(result.get(0).size() == 1);
        assertTrue(contains(result.get(0),new Token(Token.Type.OPERAND,"3")));

        assertTrue(result.get(1).size() == 1);
        assertTrue(contains(result.get(1),new Token(Token.Type.RS_UNARY_OPERATOR,"++")));

        assertTrue(result.get(2).size() == 1);
        assertTrue(contains(result.get(2),new Token(Token.Type.RS_UNARY_OPERATOR,"++")));

        assertTrue(result.get(3).size() == 2);
        assertTrue(contains(result.get(3),new Token(Token.Type.LS_UNARY_OPERATOR,"+"),new Token(Token.Type.BINARY_OPERATOR,"+")));

        assertTrue(result.get(4).size() == 1);
        assertTrue(contains(result.get(4),new Token(Token.Type.OPERAND,"4")));


    }

    private List<List<Token>> getAllTheTokens(Tokenizer.LexicalAnalizer la){

        List<List<Token>> result = new ArrayList<>();

        while (la.hasNext()) {
            List<Token> res = la.next();
            result.add(res);
        }
        return result;
    }

    private boolean contains(List<Token> list,Token ... tokens){
        for(Token t : tokens)
            if(!list.contains(t)) return false;
        return true;
    }

}
