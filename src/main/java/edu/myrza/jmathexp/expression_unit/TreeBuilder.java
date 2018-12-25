package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperator;
import edu.myrza.jmathexp.expression_unit.exp_tree_node.*;
import edu.myrza.jmathexp.expression_unit.function.Function;
import edu.myrza.jmathexp.expression_unit.operand.Operand;
import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperator;
import edu.myrza.jmathexp.expression_unit.variable.Variable;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TreeBuilder{

    /**
     * Builds math expression tree out of given tokens.
     * */
    public static ExpTreeNode build(List<Token> rpnTokens,
                                List<Function> functions,
                                List<BinaryOperator> binaryOperators,
                                List<UnaryOperator> unaryOperators,
                                Map<String,Variable> variables){

        List<ExpTreeNode> rpnNodes = convert(rpnTokens,functions,binaryOperators,unaryOperators,variables);

        Stack<ExpTreeNode> stack = new Stack<>();

        for(ExpTreeNode node : rpnNodes){
            int argc = 0;
            switch (node.getType()){
                case VARIABLE:
                case OPERAND:
                    stack.push(node);
                    continue;
                case FUNCTION           : argc = node.argc();break;
                case UNARY              : argc = 1; break;
                case BINARY             : argc = 2; break;
            }

            ExpTreeNode[] arg = new ExpTreeNode[argc];

            for(int i  = argc - 1;i >= 0; i--)
                arg[i] = stack.pop();

            for(int i = 0;i < argc; i++)
                node.addArg(i,arg[i]);

            stack.push(node);
        }

        return stack.pop();

    }

    /**
     * Converts Tokens into ASTNodes
     * */
    public static List<ExpTreeNode> convert(List<Token> rpnTokens,
                                             List<Function> functions,
                                             List<BinaryOperator> binaryOperators,
                                             List<UnaryOperator> unaryOperators,
                                             Map<String,Variable> variables)
    {

        return rpnTokens.stream()
                .map(token -> {
                    switch (token.type){
                        case VARIABLE :
                            return new VTreeNode(variables.get(token.lexeme));
                        case OPERAND  :
                            return new OTreeNode(new Operand(Double.parseDouble(token.lexeme)));
                        case LS_UNARY_OPERATOR:
                        case RS_UNARY_OPERATOR:
                            return new UOTreeNode(Stream.concat(unaryOperators.stream(),BuiltInOperators.getUnaryOperators())
                                                         .filter(uo -> uo.getLexeme().equals(token.lexeme))
                                                         .findAny()
                                                         .orElseThrow(() -> new IllegalArgumentException("unary operator " + token.lexeme + "hasn't found...")));
                        case BINARY_OPERATOR:
                            //return new BOTreeNode(factory.find(ExpUnitType.BINARY_OPERATOR,token.lexeme));
                            return new BOTreeNode(Stream.concat(binaryOperators.stream(),BuiltInOperators.getBinaryOperators())
                                                            .filter(bo -> bo.getLexeme().equals(token.lexeme))
                                                            .findFirst()
                                                            .orElseThrow(() -> new IllegalArgumentException("No such binary operator found...")));
                        case FUNCTION:
                            return new FunTreeNode(Stream.concat(functions.stream(),BuiltInFunctions.getFunctions())
                                                            .filter(f -> f.getLexeme().equals(token.lexeme))
                                                            .findFirst()
                                                            .orElseThrow(() -> new IllegalArgumentException("No such function found..." + token.lexeme)));
                        default:
                            throw new IllegalArgumentException("This token isn't supposed to be here : " + token);
                    }
                }).collect(toList());

    }

}