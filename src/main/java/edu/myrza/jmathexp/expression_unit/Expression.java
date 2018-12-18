package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperatorsBody;
import edu.myrza.jmathexp.expression_unit.function.FunctionsBody;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperatorsBody;
import edu.myrza.jmathexp.expression_unit.variable.Variable;
import edu.myrza.jmathexp.shuntingyard.ShuntingYard;
import edu.myrza.jmathexp.tokenizer.Tokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expression{

    private final ASTNode root;
    private final Map<String,Variable> variables;

    private Expression(ASTNode root,Map<String,Variable> variables){
        this.root = root;
        this.variables = variables;
    }

    public void changeExistVarsValue(String var,double newValue){
        variables.get(var).update(newValue);
    }

    public double evaluate(){
        return root.evaluate();
    }

    public static class Builder{

        private String expression;
        private Map<String,Variable> variables;
        private ExpressionUnitFactoryImpl.Builder factoryBuilder = new ExpressionUnitFactoryImpl.Builder();

        public Builder(String expression){
            this.expression = expression;
        }

        public Builder setBinaryOperator(String lexeme,
                                         boolean isLeftAssociative,
                                         int precedence,
                                         BinaryOperatorsBody body){

            factoryBuilder.addBinaryOperator(lexeme,isLeftAssociative,precedence,body);
            return this;
        }

        public Builder setUnaryOperator(String lexeme,
                                         boolean isLeftAssociative,
                                         int precedence,
                                         UnaryOperatorsBody body){

            factoryBuilder.addUnaryOperator(lexeme,isLeftAssociative,precedence,body);
            return this;
        }

        public Builder setFunction(String lexeme,
                                        int argc,
                                        FunctionsBody body){

            factoryBuilder.addFunction(lexeme,argc,body);
            return this;
        }

        public Builder setVariable(String var,double initialValue){
            if(variables == null)
                variables = new HashMap<>();

            variables.put(var,new Variable(var,initialValue));
            return this;
        }

        public Expression build(){

            ExpressionUnitFactory factory = factoryBuilder.build();
            Informator informator =  new InformatorImpl(factory);

            List<Token> tokens    = new Tokenizer(informator, variables == null ? null : variables.keySet())
                                            .tokenize(expression);

            List<Token> rpnTokens = ShuntingYard.convertToRPN(tokens,informator);

            ASTNode root = TreeBuilder.build(rpnTokens,factory,variables);

            return new Expression(root,variables);

        }

    }

}