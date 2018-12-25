package edu.myrza.jmathexp.expression_unit.exp_tree_node;

import edu.myrza.jmathexp.expression_unit.unary_operator.UnaryOperator;

public class UOTreeNode extends ExpTreeNode{

    private UnaryOperator operator;

    public UOTreeNode(UnaryOperator operator){
        this.type = Type.UNARY;
        this.operator = operator;
    }

    public int argc(){
        return 1;
    }

    public double evaluate(double ... args){
        return operator.evaluate(args);
    }
}