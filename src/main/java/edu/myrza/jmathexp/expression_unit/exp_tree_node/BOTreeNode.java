package edu.myrza.jmathexp.expression_unit.exp_tree_node;

import edu.myrza.jmathexp.expression_unit.binary_operator.BinaryOperator;

public class BOTreeNode extends ExpTreeNode{

    private BinaryOperator operator;

    public BOTreeNode(BinaryOperator operator){
        this.type = Type.BINARY;
        this.operator = operator;
    }

    public int argc(){
        return 2;
    };

    public double evaluate(double ... args){
        return operator.evaluate(args);
    };

}