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
    public void addArg(int argNum,ASTNode arg){
        if(children == null)
            children = new ArrayList<>(eu.getArgc());
        children.add(argNum,arg);
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