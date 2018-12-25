package edu.myrza.jmathexp.expression_unit.exp_tree_node;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpTreeNode {

    protected List<ExpTreeNode> children;
    protected Type type;

    public void addArg(int argNum,ExpTreeNode arg){
        if(children == null)
            children = new ArrayList<>(argc());
        children.add(argNum,arg);
    }

    public double evaluate(){
        if(children != null) {
            double[] args = children.stream()
                                    .mapToDouble(ExpTreeNode::evaluate)
                                    .toArray();

            return evaluate(args);
        }else
            return evaluate(null);
    }

    public Type getType(){
        return type;
    }

    public abstract int argc();

    protected abstract double evaluate(double ... args);

    public enum Type{
        FUNCTION,BINARY,UNARY,OPERAND,VARIABLE;
    }
}