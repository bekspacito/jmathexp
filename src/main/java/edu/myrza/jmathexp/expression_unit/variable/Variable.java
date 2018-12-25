package edu.myrza.jmathexp.expression_unit.variable;


public class Variable{

    private final String variable;
    private double value;

    public Variable(String variable,double value){
        this.variable = variable;
        this.value = value;
    }

    public void update(double newValue){
        this.value = newValue;
    }

    public double evaluate(double ... args) {
        return value;
    }

    public String getLexeme() {
        return variable;
    }
}