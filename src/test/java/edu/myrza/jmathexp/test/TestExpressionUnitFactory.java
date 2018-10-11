package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.expression_unit.*;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class TestExpressionUnitFactory{

    static ExpressionUnitFactory factory;

    @BeforeClass
    public static void init(){

        Map<String,FunctionEU> customFunctions = new HashMap<>();
        customFunctions.put("arcsin",new FunctionEU("arcsin",1,args ->  Math.asin(args[0]) ));

        Map<String,UnaryOperatorEU> customUnaryOperators = new HashMap<>();
        customUnaryOperators.put("!",new UnaryOperatorEU("!",BuiltInOperators.POWER_PRECEDENCE + 1,arg -> {

            if(arg % 1 != 0) throw new IllegalArgumentException("! operand must be integer...");
            if(arg <= 0)     throw new IllegalArgumentException("fact defined only for values above zero....");

            int intArg = (int)arg;

            return IntStream.rangeClosed(1,intArg)
                            .reduce((i,j) -> i*j)
                            .getAsInt();

        }));

        factory = new ExpressionUnitFactoryImpl(customFunctions,
                                                null,
                                                customUnaryOperators);


    }

    /**
     * Returns and executes factorial
     * */
    @Test
    public void testOne(){

        ExpressionUnit fact = factory.createExpressionUnit(EUType.UNARY_OPERATOR,"!");

        assertNotNull(fact);

        double result = fact.evaluate(5);

        assertEquals(120.0,result,0.0);

    }

    /**
     * Returns and executes arcsin
     * */
    @Test
    public void testTwo(){

        ExpressionUnit arcsin = factory.createExpressionUnit(EUType.FUNCTION,"arcsin");

        assertNotNull(arcsin);

        double result = Math.toDegrees(arcsin.evaluate(1));

        assertEquals(90.0,result,0.0);

    }


}