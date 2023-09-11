package com.witek.model.functions;

import com.witek.model.OptimizationFunction;

public class Griewank implements OptimizationFunction {
    @Override
    public double optimize(double[] arguments) {
        double sum = 0;
        double power = 1;
        for(int i = 0 ; i < arguments.length ; i++){
            double x = arguments[i];
            sum += Math.pow(x,2);
            double phrase = x/Math.sqrt(i+1);
            power *= Math.cos(phrase);
        }
        return 1/4000.0 * sum - power + 1;
    }

    @Override
    public String toString(){
        return "Griewank";
    }
}
