package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.*;
import edu.myrza.jmathexp.shuntingyard.ShuntingYard;
import edu.myrza.jmathexp.tokenizer.Tokenizer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestEvaluation {

    @Test
    public void test1(){

        assertEquals(35.0,evaluate("5*(3+4)"),0.01);

    }

    @Test
    public void test2(){

        assertEquals(1.0,evaluate("sin(3/2)*sin(3/2) + cos(3/2)*cos(3/2)"),0.0001);

    }

    @Test
    public void test3(){

        assertEquals(-32.0,evaluate("-2^5"),0.0001);

    }

    @Test
    public void test4(){

        assertEquals(8.0,evaluate("2!+3!"),0.0001);
        assertEquals(4.0,evaluate("3!-2!"),0.0001);
        assertEquals(6.0,evaluate("3!"),0.0001);
        assertEquals(720.0,evaluate("3!!"),0.0001);
        assertEquals(10.0,evaluate("4+3!"),0.0001);
        assertEquals(12.0,evaluate("3!*2"),0.0001);
        assertEquals(12.0,evaluate("2*3!"),0.0001);
        assertEquals(22.0,evaluate("4 + 3! + 2 * 6"),0.0001);

    }

    private static double evaluate(String exp){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                .addUnaryOperator("!",true,BuiltInOperators.POWER_PRECEDENCE + 1,argDouble -> {

                    final int arg = (int) argDouble;
                    if ((double) arg != argDouble) {
                        throw new IllegalArgumentException("Operand for factorial has to be an integer");
                    }
                    if (arg < 0) {
                        throw new IllegalArgumentException("The operand of the factorial can not be less than zero");
                    }
                    double result = 1;
                    for (int i = 1; i <= arg; i++) {
                        result *= i;
                    }
                    return result;

                }).build();
        Informator informator =  new InformatorImpl(factory);

        Tokenizer tokenizer = new Tokenizer(informator, null);


        List<Token> res = tokenizer.tokenize(exp);
        res = ShuntingYard.convertToRPN(res,informator);
        List<ASTNode> nodes = TokenASTNodeConverter.convert(res,factory,null);
        ASTNode otvet = TreeBuilder.build(nodes);

        System.out.println(otvet.evaluate());
        return otvet.evaluate();

    }

}
