package edu.myrza.jmathexp.expression_unit;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

public class ASTNode{

    private List<ASTNode> children;
    private final ExpressionUnit eu;

    public ASTNode(ExpressionUnit eu){
        this.eu = eu;
    }

    /**
     * todo : this method seems counter-intuitive
     * do somethinf about it...
     * */
    public void addChild(ASTNode child){
        if(children == null) children = new ArrayList<>();
        children.add(child);
    }


    public ExpressionUnit getExpressionUnit(){
        return eu;
    }

    public double evaluate(){
        if(children != null) {
            double[] args = children.stream()
                    .mapToDouble(ASTNode::evaluate)
                    .toArray();

            return eu.evaluate(args);
        }else
            return eu.evaluate();
    }
}