package edu.myrza.jmathexp.expression_unit.exp_tree_node;

import edu.myrza.jmathexp.expression_unit.operand.Operand;

public class OTreeNode extends ExpTreeNode{

    private Operand o;

    public OTreeNode(Operand o){
        this.type = Type.OPERAND;
        this.o = o;
    }

    public int argc(){
        return 0;
    }

    public double evaluate(double ... args){
        return o.getValue();
    }
}