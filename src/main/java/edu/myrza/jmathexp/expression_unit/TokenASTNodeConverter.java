package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.common.Token;
import edu.myrza.jmathexp.expression_unit.operand.Operand;
import edu.myrza.jmathexp.expression_unit.variable.Variable;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class TokenASTNodeConverter{

    public static List<ASTNode> convert(List<Token> rpnTokens,
                                        ExpressionUnitFactory factory,
                                        Map<String,Double> variables)
    {

        return rpnTokens.stream()
                         .map(token -> {
                             switch (token.type){
                                 case VARIABLE :
                                     return new ASTNode(new Variable(token.lexeme,variables));

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