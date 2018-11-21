package edu.myrza.jmathexp.test;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.BuiltInOperators;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactory;
import edu.myrza.jmathexp.expression_unit.ExpressionUnitFactoryImpl;
import edu.myrza.jmathexp.expression_unit.InformatorImpl;
import org.junit.Test;

import java.util.stream.IntStream;

import static edu.myrza.jmathexp.expression_unit.BuiltInOperators.POWER_PRECEDENCE;
import static org.junit.Assert.*;


public class TestInformator {

    Informator informator;

    public TestInformator(){

        ExpressionUnitFactory factory = new ExpressionUnitFactoryImpl.Builder()
                .addUnaryOperator("!",true,POWER_PRECEDENCE + 1,arg -> {
                    int intArg = (int)arg;
                    return IntStream.rangeClosed(1,intArg)
                            .reduce((i,j) -> i*j)
                            .getAsInt();
                })
                .addUnaryOperator("++",true,POWER_PRECEDENCE + 100,arg -> arg + 1)
                .build();

        informator = new InformatorImpl(factory);

    }

    @Test
    //test operator
    public void test1(){

        Token testToken = new Token(Token.Type.BINARY_OPERATOR,"+");
        int precedence = informator.priority(testToken);
        boolean isLeftAssociated = informator.isLeftAssociated(testToken);

        assertEquals(BuiltInOperators.ADDITION_PRECEDENCE,precedence);
        assertEquals(true,isLeftAssociated);

    }

    @Test
    //test function
    public void test2(){

        Token testToken = new Token(Token.Type.FUNCTION,"cos");
        int argc = informator.funcArgc(testToken);

        assertEquals(1,argc);

    }

    @Test
    public void test3(){

        Token testToken = new Token(Token.Type.RS_UNARY_OPERATOR,"!");
        boolean isLeftAssociated = informator.isLeftAssociated(testToken);
        int priority = informator.priority(testToken);

        assertEquals(true,isLeftAssociated);
        assertEquals(POWER_PRECEDENCE + 1,priority);
    }

}
