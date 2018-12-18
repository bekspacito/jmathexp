package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.operand.Operand;
import edu.myrza.jmathexp.expression_unit.variable.Variable;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import static java.util.stream.Collectors.toList;

public class TreeBuilder{

    /**
     * Builds math expression tree out of given tokens.
     * */
    public static ASTNode build(List<Token> rpnTokens,
                                ExpressionUnitFactory factory,
                                Map<String,Variable> variables){

        List<ASTNode> rpnNodes = convert(rpnTokens,factory,variables);

        Stack<ASTNode> stack = new Stack<>();

        for(ASTNode node : rpnNodes){
            ExpressionUnit eu = node.getExpressionUnit();
            int argc = 0;
            switch (eu.getType()){
                case VARIABLE:
                case OPERAND:
                    stack.push(node);
                    continue;
                case FUNCTION           : argc = eu.getArgc();break;
                case UNARY_OPERATOR     : argc = 1; break;
                case BINARY_OPERATOR    : argc = 2; break;
            }

            ASTNode[] arg = new ASTNode[argc];

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
    public static List<ASTNode> convert(List<Token> rpnTokens,
                                        ExpressionUnitFactory factory,
                                        Map<String,Variable> variables)
    {

        return rpnTokens.stream()
                .map(token -> {
                    switch (token.type){
                        case VARIABLE :
                            return new ASTNode(variables.get(token.lexeme));
                        case OPERAND  :
                            return new ASTNode(new Operand(Double.parseDouble(token.lexeme)));
                        case LS_UNARY_OPERATOR:
                        case RS_UNARY_OPERATOR:
                            return new ASTNode(factory.find(ExpUnitType.UNARY_OPERATOR,token.lexeme));
                        case BINARY_OPERATOR:
                            return new ASTNode(factory.find(ExpUnitType.BINARY_OPERATOR,token.lexeme));
                        case FUNCTION:
                            return new ASTNode(factory.find(ExpUnitType.FUNCTION,token.lexeme));
                        default:
                            throw new IllegalArgumentException("This token isn't supposed to be here : " + token);
                    }
                }).collect(toList());

    }

}