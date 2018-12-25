package edu.myrza.jmathexp.expression_unit.exp_tree_node;

import edu.myrza.jmathexp.expression_unit.variable.Variable;

public class VTreeNode extends ExpTreeNode{

    private Variable variable;

    public VTreeNode(Variable variable){
        this.type = Type.VARIABLE;
        this.variable = variable;
    }

    public int argc(){
        return 0;
    }

    public double evaluate(double ... args){
        return variable.evaluate();
    }

}