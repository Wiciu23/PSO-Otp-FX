package com.witek.model;

public class OptimizeFunctionFactory {
    public static OptimizationFunction getOptimizeFunction(int type){
        if(type == 1){
            OptimizationFunction function = new FunctionMgr();
            return function;
        }else
            return null;
    }
}
