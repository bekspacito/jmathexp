package edu.myrza.jmathexp.test;

import org.junit.Test;

import static edu.myrza.jmathexp.expression_unit.BuiltInOperators.*;
import static org.junit.Assert.*;

import edu.myrza.jmathexp.expression_unit.*;

import java.util.stream.IntStream;

public class TestExpressionUnitFactory{

    @Test
    public void testCustomFunc(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                                                .addFunction("log",2,args -> Math.log(args[1])/Math.log(args[0]))
                                                .build();


        ExpressionUnit log = factory.create(ExpUnitType.FUNCTION,"log");
        assertNotNull(log);
        assertEquals(4.0,log.evaluate(2,16),0.0);

    }

    @Test
    public void testCustomBinOp(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                .addBinaryOperator("<<",true,POWER_PRECEDENCE + 1,(op1,op2) -> {

                    int intOp1 = (int)op1;
                    int intOp2 = (int)op2;

                    return intOp1 << intOp2;

                })
                .build();

        ExpressionUnit bitwiseShift = factory.create(ExpUnitType.BINARY_OPERATOR,"<<");
        assertNotNull(bitwiseShift);
        assertEquals(1024.0,bitwiseShift.evaluate(512,1),0.01);
        assertEquals(2048.0,bitwiseShift.evaluate(512,2),0.01);


    }

    @Test
    public void testCustomUnaryOp(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                  .addUnaryOperator("!",true,POWER_PRECEDENCE + 1,arg -> {

                      int intArg = (int)arg;

                      return IntStream.rangeClosed(1,intArg)
                                      .reduce((i,j) -> i*j)
                                      .getAsInt();

                  })
                  .build();

        ExpressionUnit fact = factory.create(ExpUnitType.UNARY_OPERATOR,"!");
        assertNotNull(fact);
        assertEquals(120.0,fact.evaluate(5),0.001);
        assertEquals(3628800.0,fact.evaluate(10),0.001);

    }

    @Test
    public void testBuiltInFunc(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                                                                     .build();

        ExpressionUnit sqrt = factory.create(ExpUnitType.FUNCTION,"sqrt");
        assertNotNull(sqrt);
        assertEquals(8.0,sqrt.evaluate(64),0.0001);

    }


}