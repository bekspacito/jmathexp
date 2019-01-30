package edu.myrza.jmathexp.expression_unit;

import edu.myrza.jmathexp.expression_unit.function.Function;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class BuiltInFunctions {

    private static Map<String,Function> globalFunctions = new HashMap<>();

    static {

        globalFunctions.put("abs",new Function("abs",1, args -> Math.abs(args[0])));

        globalFunctions.put("sqrt",new Function("sqrt",1, args -> {

           if(args[0] <= 0d)
               throw new IllegalArgumentException("The argument of sqrt has to be positive...");

           return Math.sqrt(args[0]);

        }));

        globalFunctions.put("log",new Function("log",1, args -> {

            if(args[0] <= 0d)
                throw new IllegalArgumentException("The argument of log has to be positive...");

            return Math.log(args[0]);
        }));

        globalFunctions.put("log10",new Function("log10",1, args -> {

            if(args[0] <= 0d)
                throw new IllegalArgumentException("The argument of log10 has to be positive...");

            return Math.log10(args[0]);
        }));

        globalFunctions.put("log2",new Function("log2",1, args -> {
            if(args[0] <= 0d)
                throw new IllegalArgumentException("The argument of log10 has to be positive...");
            return Math.log(args[0])/Math.log(2);
        }));

        globalFunctions.put("sin",new Function("sin",1, args -> Math.sin(args[0])));

        globalFunctions.put("cos",new Function("cos",1, args -> Math.cos(args[0])));
    }

    public static Function getFunction(String name){ return globalFunctions.get(name); }

    public static Stream<Function> getFunctions(){ return globalFunctions.values().stream(); }

    public static Set<String> getFunctionNames(){
        return globalFunctions.keySet();
    }

}