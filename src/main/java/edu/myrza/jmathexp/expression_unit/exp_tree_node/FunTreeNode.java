package edu.myrza.jmathexp.expression_unit.exp_tree_node;

import edu.myrza.jmathexp.expression_unit.function.Function;

public class FunTreeNode extends ExpTreeNode{

    private Function function;

    public FunTreeNode(Function function){
        this.type = Type.FUNCTION;
        this.function = function;
    }

    public int argc(){
        return function.getArgc();
    }

    public double evaluate(double ... args){
        return function.evaluate(args);
    }

}