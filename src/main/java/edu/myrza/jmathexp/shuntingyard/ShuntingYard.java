package edu.myrza.jmathexp.shuntingyard;

import edu.myrza.jmathexp.common.Informator;
import edu.myrza.jmathexp.common.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.util.Arrays.*;

/**
 * Parses an expression specified in infix notation into reverse
 * polish notation.
 *
 * */

public class ShuntingYard{

    private static List<Token.Type> operatorTypes = asList(Token.Type.RS_UNARY_OPERATOR,
                                                           Token.Type.LS_UNARY_OPERATOR,
                                                           Token.Type.BINARY_OPERATOR);

    public static List<Token> convertToRPN(List<Token> infixRep,Informator info){

        List<Token>  output = new ArrayList<>();
        Stack<Token> stack  = new Stack<>();

        for(Token current : infixRep)
            switch (current.type){
                case OPERAND:
                    output.add(current);
                    break;

                case FUNCTION:
                case OPEN_PARENTHESES:
                    stack.push(current);
                    break;

                case RS_UNARY_OPERATOR:
                case LS_UNARY_OPERATOR:
                case BINARY_OPERATOR:

                    while (!stack.empty() && operatorTypes.contains(stack.peek().type)) {
                        Token o1 = current;
                        Token o2 = stack.peek();
                        if (o1.type != Token.Type.BINARY_OPERATOR && o2.type == Token.Type.BINARY_OPERATOR) {
                            break;
                        } else if ((info.isLeftAssociated(o1) && info.priority(o1) <= info.priority(o2))
                                || (info.priority(o1) < info.priority(o2))) {
                            output.add(stack.pop());
                        }else {
                            break;
                        }
                    }
                    stack.push(current);
                    break;

                case CLOSE_PARENTHESES:

                    while (stack.peek().type != Token.Type.OPEN_PARENTHESES) {
                        output.add(stack.pop());
                    }
                    stack.pop();
                    if (!stack.isEmpty() && stack.peek().type == Token.Type.FUNCTION) {
                        output.add(stack.pop());
                    }
                    break;

                case FUNCTION_ARG_SEPARATOR:

                    while (!stack.empty() && stack.peek().type != Token.Type.OPEN_PARENTHESES) {
                        output.add(stack.pop());
                    }
                    break;

            }

        while (!stack.empty())
            output.add(stack.pop());

        return output;
    }

}