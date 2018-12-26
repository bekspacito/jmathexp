package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.expression_unit.*;
import org.junit.Test;


import static org.junit.Assert.*;

public class TestEvaluation {

    @Test
    public void test1(){

        assertEquals(35.0,evaluate("5*(3+4)"),0.01);

    }

    @Test
    public void test2(){

        Expression expression = new Expression.Builder("sin(3/2)^2 + cos(3/2)^2")
                                               .build();

        assertEquals(1.0,expression.evaluate(),0.0001);

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

    @Test
    public void  test5(){

        Expression expression = new Expression.Builder("2*x")
                                                .setVariable("x",2d)
                                                .build();

        assertEquals(4.0,expression.evaluate(),0);

    }

    @Test
    public void test6(){

        Expression expression = new Expression.Builder("-3*x + 4!/y + log(2,64)")
                                                .setVariable("x",15)
                                                .setVariable("y",4)
                                                .setFunction("log",2,args -> {
                                                        return Math.log(args[1])/Math.log(args[0]);
                                                    }
                                                ).setUnaryOperator("!",
                                                            true,
                                                                BuiltInOperators.POWER_PRECEDENCE + 1,
                                                                    argDouble -> {
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

                                                                    }
                                                ).build();

        assertEquals(-33.0,expression.evaluate(),0.001);

        expression.changeExistVarsValue("x",3);
        expression.changeExistVarsValue("y",10);

        assertEquals(-0.6,expression.evaluate(),0.001);

    }

    @Test
    public void test7(){

        assertEquals(-12.0,evaluate("-5+-7"),0.001);
        assertEquals(-2.0,evaluate("5+-7"),0.001);
        assertEquals(2.0,evaluate("-5--7"),0.001);
        assertEquals(2.0,evaluate("-5-(-7)"),0.001);
        assertEquals(35.0,evaluate("-5*-7"),0.001);
        assertEquals(0.0,evaluate("0/-7"),0.001);
    }

    @Test
    public void test8(){

        assertEquals(1.0,evaluate("sin(pi/2)"),0);

    }

    private static double evaluate(String exp){

        return new Expression.Builder(exp)
                                .setUnaryOperator("!",
                                        true,
                                        BuiltInOperators.POWER_PRECEDENCE + 1,
                                        argDouble -> {

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


                                        })
                                .build()
                                .evaluate();


    }

}
