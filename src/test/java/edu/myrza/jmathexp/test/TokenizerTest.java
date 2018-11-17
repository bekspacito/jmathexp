package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.expression_unit.ExpUnitType;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactoryImpl;
import edu.myrza.jmathexp.token.Token;
import edu.myrza.jmathexp.token.Tokenizer;

import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static edu.myrza.jmathexp.expression_unit.BuiltInOperators.*;

public class TokenizerTest {

    private Tokenizer tokenizer;

    public TokenizerTest(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                                                                     .addUnaryOperator("!",true,POWER_PRECEDENCE + 1,arg -> {
                                                                         int intArg = (int)arg;
                                                                         return IntStream.rangeClosed(1,intArg)
                                                                                         .reduce((i,j) -> i*j)
                                                                                         .getAsInt();
                                                                     })
                                                                     .build();

        Set<String> functionNames = factory.getIds(ExpUnitType.FUNCTION);
        Set<String> binOpNames    = factory.getIds(ExpUnitType.BINARY_OPERATOR);
        Set<String> rightSideUnOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                                                                                   .filter(eu -> factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                                                                                   .collect(toSet());

        Set<String> leftSideUnOpNames = factory.getIds(ExpUnitType.UNARY_OPERATOR).stream()
                                                                                  .filter(eu -> !factory.create(ExpUnitType.UNARY_OPERATOR,eu).isLeftAssociative())
                                                                                  .collect(toSet());

        tokenizer = new Tokenizer(factory);
    }

    @Test
    //tokenize expression -3+4!
    public void test1(){

        String exp = "-3+4!";

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(5,result.size());

        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),result.get(1));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),result.get(2));
        assertEquals(new Token(Token.Type.OPERAND,"4"),result.get(3));
        assertEquals(new Token(Token.Type.RS_UNARY_OPERATOR,"!"),result.get(4));
    }

    @Test
    public void test2(){

        String exp = "-3+-4";

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(5,result.size());

        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),result.get(1));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),result.get(2));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(3));
        assertEquals(new Token(Token.Type.OPERAND,"4"),result.get(4));


    }

    @Test
    public void test3(){

        String exp = "---++-3";

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(7,result.size());

        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(0));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(1));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(2));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"+"),result.get(3));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"+"),result.get(4));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(5));
        assertEquals(new Token(Token.Type.OPERAND,"3"),result.get(6));

    }

    @Test
    public void test5(){

        String exp = "3+-1";

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(4,result.size());

        assertEquals(new Token(Token.Type.OPERAND,"3"),result.get(0));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),result.get(1));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(2));
        assertEquals(new Token(Token.Type.OPERAND,"1"),result.get(3));

    }

    @Test
    public void test6(){

        String exp = "3+-1-0.32++2";

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(9,result.size());

        assertEquals(new Token(Token.Type.OPERAND,"3"),result.get(0));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),result.get(1));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(2));
        assertEquals(new Token(Token.Type.OPERAND,"1"),result.get(3));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"-"),result.get(4));
        assertEquals(new Token(Token.Type.OPERAND,"0.32"),result.get(5));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),result.get(6));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"+"),result.get(7));
        assertEquals(new Token(Token.Type.OPERAND,"2"),result.get(8));


    }

    @Test
    public void test7(){

        String exp = "log(1)";

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(4,result.size());

        assertEquals(new Token(Token.Type.FUNCTION,"log"),result.get(0));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"1"),result.get(2));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(3));

    }

    //"log(x) - y * (sqrt(x^cos(y)))"
    @Test
    public void test8(){

        String exp = "log(0.27) - 123.33 * (sqrt(11.75^cos(0.0015)))"; //18

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(18,result.size());

        assertEquals(new Token(Token.Type.FUNCTION,"log"),result.get(0));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"0.27"),result.get(2));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(3));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"-"),result.get(4));
        assertEquals(new Token(Token.Type.OPERAND,"123.33"),result.get(5));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"*"),result.get(6));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(7));
        assertEquals(new Token(Token.Type.FUNCTION,"sqrt"),result.get(8));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(9));
        assertEquals(new Token(Token.Type.OPERAND,"11.75"),result.get(10));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),result.get(11));
        assertEquals(new Token(Token.Type.FUNCTION,"cos"),result.get(12));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(13));
        assertEquals(new Token(Token.Type.OPERAND,"0.0015"),result.get(14));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(15));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(16));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(17));

    }

    @Test
    public void test9(){

        String exp = "--2 * (-14)"; //8

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(8,result.size());

        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(0));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"2"),result.get(2));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"*"),result.get(3));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(4));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(5));
        assertEquals(new Token(Token.Type.OPERAND,"14"),result.get(6));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(7));

    }

    @Test
    public void test10(){

        String exp = "2 * 4 + - log ( 3 )"; //9

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(9,result.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),result.get(0));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"*"),result.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"4"),result.get(2));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),result.get(3));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),result.get(4));
        assertEquals(new Token(Token.Type.FUNCTION,"log"),result.get(5));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(6));
        assertEquals(new Token(Token.Type.OPERAND,"3"),result.get(7));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(8));

    }

    @Test
    public void test11(){

        String exp = "log2(4)"; //4

        List<Token> result = tokenizer.tokenize(exp);

        assertNotNull(result);
        assertEquals(4,result.size());

        assertEquals(new Token(Token.Type.FUNCTION,"log2"),result.get(0));
        assertEquals(new Token(Token.Type.OPEN_PARENTHESES,"("),result.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"4"),result.get(2));
        assertEquals(new Token(Token.Type.CLOSE_PARENTHESES,")"),result.get(3));

    }


}