package edu.myrza.jmathexp.expression_unit;

import java.util.List;
import java.util.Stack;

public class TreeBuilder{

    public static ASTNode build(List<ASTNode> rpnNodes){

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

            ASTNode[] children = new ASTNode[argc];

            for(int i  = argc - 1;i >= 0; i--)
                children[i] = stack.pop();

            for(int i = 0;i < argc; i++)
                node.addChild(children[i]);

            stack.push(node);
        }

        return stack.pop();
    }

}