package com.witek.model;

import com.witek.model.functions.Ackley;
import com.witek.model.functions.Griewank;
import com.witek.model.functions.Rastrigin;

public class OptimizeFunctionFactory {
    public static OptimizationFunction getOptimizeFunction(int type){
        if(type == 1){
            OptimizationFunction function = new FunctionMgr();
            return function;
        }else if(type == 2){
            OptimizationFunction function = new Rastrigin();
            return function;
        } else if(type == 3){
            OptimizationFunction function = new Ackley();
            return function;
        }else if(type == 4){
            OptimizationFunction function = new Griewank();
            return function;
        }else
            return null;
    }
}
