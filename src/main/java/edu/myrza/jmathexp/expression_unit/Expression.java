package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperator;
import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperatorsBody;
import edu.myrza.jmathexp.expression_unit.exp_tree_node.ExpTreeNode;
import edu.myrza.jmathexp.expression_unit.function.Function;
import edu.myrza.jmathexp.expression_unit.function.FunctionsBody;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperator;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperatorsBody;
import edu.myrza.jmathexp.expression_unit.variable.Variable;
import edu.myrza.jmathexp.shuntingyard.ShuntingYard;
import edu.myrza.jmathexp.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expression{

    private final ExpTreeNode root;
    private final Map<String,Variable> variables;

    private Expression(ExpTreeNode root,
                       Map<String,Variable> variables){

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
        private List<Function> functions;
        private List<BinaryOperator> binaryOperators;
        private List<UnaryOperator> unaryOperators;


        public Builder(String expression){
            this.expression = expression;
            this.functions = new ArrayList<>();
            this.binaryOperators = new ArrayList<>();
            this.unaryOperators = new ArrayList<>();
            this.variables = new HashMap<>();
            variables.put("pi",new Variable("pi",3.14159265359));
            variables.put("e" ,new Variable("e",2.71828182846));
        }

        public Builder setBinaryOperator(String lexeme,
                                         boolean isLeftAssociative,
                                         int precedence,
                                         BinaryOperatorsBody body){

            binaryOperators.add(new BinaryOperator(lexeme,isLeftAssociative,precedence,body));
            return this;
        }

        public Builder setUnaryOperator(String lexeme,
                                         boolean isLeftAssociative,
                                         int precedence,
                                         UnaryOperatorsBody body){

            unaryOperators.add(new UnaryOperator(lexeme,isLeftAssociative,precedence,body));
            return this;
        }

        public Builder setFunction(String lexeme,
                                        int argc,
                                        FunctionsBody body){
            variables.keySet().stream()
                      .filter(varLex -> lexeme.equals(varLex))
                      .findAny()
                      .ifPresent(lex -> {
                          throw new IllegalArgumentException("variable's lexeme cannot match with any of function's lexemes : " + lex);
                      });

            functions.add(new Function(lexeme,argc,body));
            return this;
        }

        public Builder setVariable(String varLexeme,double initialValue){

            functions.stream()
                     .map(Function::getLexeme)
                     .filter(lex -> lex.equals(varLexeme))
                     .findAny()
                     .ifPresent(lex -> {
                         throw new IllegalArgumentException("variable's lexeme cannot match with any of function's lexemes : " + lex);
                     });

            variables.put(varLexeme,new Variable(varLexeme,initialValue));
            return this;
        }

        public Expression build(){

            Informator informator = new InformatorImpl(functions,binaryOperators,unaryOperators);

            List<Token> tokens    = new Tokenizer(informator,variables.keySet())
                                            .tokenize(expression);

            List<Token> rpnTokens = ShuntingYard.convertToRPN(tokens,informator);

            ExpTreeNode root = TreeBuilder.build(rpnTokens,functions,binaryOperators,unaryOperators,variables);

            return new Expression(root,variables);

        }

    }

}