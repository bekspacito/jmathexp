package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.expression_unit.BuiltInOperators;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;
import org.junit.Test;
import static org.junit.Assert.*;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactoryImpl;
import edu.myrza.jmathexp.expression_unit.InformatorImpl;
import edu.myrza.jmathexp.shuntingyard.ShuntingYard;
import edu.myrza.jmathexp.tokenizer.Tokenizer;

import java.util.*;

public class TestShuntingYard{

    private Tokenizer tokenizer;
    private Informator informator;

    public TestShuntingYard(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                                                                     .addUnaryOperator("!",true, BuiltInOperators.POWER_PRECEDENCE + 1,arg -> {
                                                                         final int temp = (int) arg;
                                                                         if ((double) temp != arg) {
                                                                             throw new IllegalArgumentException("Operand for factorial has to be an integer");
                                                                         }
                                                                         if (temp < 0) {
                                                                             throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                                                                         }
                                                                         double result = 1;
                                                                         for (int i = 1; i <= temp; i++) {
                                                                             result *= i;
                                                                         }
                                                                         return result;

                                                                     })
                                                                     .addFunction("max",3,args -> Arrays.stream(args).max().getAsDouble())
                                                                     .build();

        informator =  new InformatorImpl(factory);

        tokenizer = new Tokenizer(informator, null);

    }

    @Test
    public void test1(){

        List<Token> res = tokenizer.tokenize("2+3");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(3,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),res.get(2));

    }

    @Test
    public void test2(){

        List<Token> res = tokenizer.tokenize("-3");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(2,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(0));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(1));

    }

    @Test
    public void test3(){

        List<Token> res = tokenizer.tokenize("-2^3");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(4,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(2));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(3));

    }

    @Test
    public void test4(){

        List<Token> res = tokenizer.tokenize("4^5^6^7");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(7,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"4"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"5"),res.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"6"),res.get(2));
        assertEquals(new Token(Token.Type.OPERAND,"7"),res.get(3));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(4));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(5));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(6));

    }

    @Test
    public void test5(){

        List<Token> res = tokenizer.tokenize("2^-3");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(4,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(2));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(3));

    }

    @Test
    public void test6(){

        List<Token> res = tokenizer.tokenize("2^---+3");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(7,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"+"),res.get(2));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(3));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(4));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(5));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(6));

    }

    @Test
    public void test7(){

        List<Token> res = tokenizer.tokenize("2^-3!");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(5,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.RS_UNARY_OPERATOR,"!"),res.get(2));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(3));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(4));

    }

    @Test
    public void test8(){

        List<Token> res = tokenizer.tokenize("2^-3!+sqrt(4)");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(8,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.RS_UNARY_OPERATOR,"!"),res.get(2));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(3));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(4));
        assertEquals(new Token(Token.Type.OPERAND,"4"),res.get(5));
        assertEquals(new Token(Token.Type.FUNCTION,"sqrt"),res.get(6));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),res.get(7));

    }

    @Test
    public void test9(){

        List<Token> res = tokenizer.tokenize("max(1+3,4^2,5*6)");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(10,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"1"),res.get(0));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(1));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"+"),res.get(2));

        assertEquals(new Token(Token.Type.OPERAND,"4"),res.get(3));
        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(4));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(5));

        assertEquals(new Token(Token.Type.OPERAND,"5"),res.get(6));
        assertEquals(new Token(Token.Type.OPERAND,"6"),res.get(7));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"*"),res.get(8));

        assertEquals(new Token(Token.Type.FUNCTION,"max"),res.get(9));

        res.stream().forEach(System.out::println);
    }

    @Test
    public void test10(){

        List<Token> res = tokenizer.tokenize("max(sqrt(169),4^2,5*6)");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(9,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"169"),res.get(0));
        assertEquals(new Token(Token.Type.FUNCTION,"sqrt"),res.get(1));

        assertEquals(new Token(Token.Type.OPERAND,"4"),res.get(2));
        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(3));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(4));

        assertEquals(new Token(Token.Type.OPERAND,"5"),res.get(5));
        assertEquals(new Token(Token.Type.OPERAND,"6"),res.get(6));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"*"),res.get(7));

        assertEquals(new Token(Token.Type.FUNCTION,"max"),res.get(8));

        res.stream().forEach(System.out::println);

    }

    @Test
    public void test11(){

        List<Token> res = tokenizer.tokenize("(-2)^3");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(4,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.LS_UNARY_OPERATOR,"-"),res.get(1));
        assertEquals(new Token(Token.Type.OPERAND,"3"),res.get(2));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"^"),res.get(3));

    }

    @Test
    public void test12(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder().build();
        Informator informator =  new InformatorImpl(factory);

        Map<String,Double> variables = new HashMap<>();
        variables.put("x",3.1415);

        Tokenizer tokenizer = new Tokenizer(informator, variables.keySet());


        List<Token> res = tokenizer.tokenize("2*x");
        res = ShuntingYard.convertToRPN(res,informator);

        assertNotNull(res);
        assertEquals(3,res.size());

        assertEquals(new Token(Token.Type.OPERAND,"2"),res.get(0));
        assertEquals(new Token(Token.Type.VARIABLE,"x"),res.get(1));
        assertEquals(new Token(Token.Type.BINARY_OPERATOR,"*"),res.get(2));

    }

}