package com.witek.model;

import java.util.Random;

/**
 * Represents a particle from the Particle Swarm Optimization algorithm.
 */
class Particle{
    //Dodawanie tutaj obserwatorów za pomoca metody fabrykującej ? -> każdy obserwator będzie modyfikował wykres (utworzyć coś z synchronisation ?
    // by tylko jeden miał dostęp ?
    
    private Vector position;        // Current position.
    private Vector velocity;
    private Vector bestPosition;    // Personal best solution.
    private double bestEval;        // Personal best value.
    private OptimizationFunction function;  // The evaluation function to use.
    private OptimizationParameter[] parameters;

    Particle (OptimizationFunction function, OptimizationParameter[] parameters) {
        int vectorLength = parameters.length;
        this.function = function;
        this.parameters = parameters;
        position = new Vector(vectorLength);
        velocity = new Vector(vectorLength);
        setRandomPosition();
        bestPosition = velocity.clone();
        bestEval = eval();
        //Tutaj dodać metodę fabrykująca ?
    }

    /**
     * The evaluation of the current position.
     * @return      the evaluation
     */
    
    
    //Dramat, hermetyzacja tego, napisać może jakas metodę abstrakcyjną ? określic interfejs i na podstawie tego interfejsu tworzyć implementacje 
    
    private double eval () {
        double[] arguments = position.getCordinates();
        return this.function.optimize(arguments);
    }

    // Wypierdolić to i np utworzyć tutaj gnerowanie randomów (UPEWNIĆ SIĘ CZY GENEROWANIE TEGO W SWARM nie przyniesie korzyści (parametry dodatkowe ))
     private void setRandomPosition () {
        int vectorLength = this.parameters.length;
        double[] randomPosition = new double[vectorLength];

        for(int i = 0 ; i < vectorLength ; i++){
            double upRange = parameters[i].getUpperBound();
            double downRange = parameters[i].getLowerBound();
            randomPosition[i] = rand(downRange,upRange);
        }

        //dodac na sztywno generowanie zakresów parametru a jako tablicy!!! i do position set wcodzi tablica parametru a [OK]
        this.position.set(randomPosition);
    }

    /**
     * Generate a random number between a certain range.
     * @param beginRange    the minimum value (inclusive)
     * @param endRange      the maximum value (exclusive)
     * @return              the randomly generated value
     */
    private static double rand (double beginRange, double endRange) {
        Random r = new Random();
        return beginRange + (r.nextDouble()*(endRange-beginRange));
    }

    private static double rand2 (double beginRange, double endRange){
        Random r = new Random();
        return beginRange + (r.nextDouble()/(1.0))*endRange;
    }

    /**
     * Update the personal best if the current evaluation is better.
     */
    void updatePersonalBest () {
        double eval = eval();
        if (eval < bestEval) {
            bestPosition = position.clone();
            bestEval = eval;
        }
    }

    /**
     * Get a copy of the position of the particle.
     * @return  the x position
     */
    Vector getPosition () {
        return position.clone();
    }

    /**
     * Get a copy of the velocity of the particle.
     * @return  the velocity
     */
    Vector getVelocity () {
        return velocity.clone();
    }

    /**
     * Get a copy of the personal best solution.
     * @return  the best position
     */
    Vector getBestPosition() {
        return bestPosition.clone();
    }

    /**
     * Get the value of the personal best solution.
     * @return  the evaluation
     */
    double getBestEval () {
        return bestEval;
    }

    /**
     * Update the position of a particle by adding its velocity to its position.
     */
    void updatePosition () {
        this.position.add(velocity);
    }

    /**
     * Set the velocity of the particle.
     * @param velocity  the new velocity
     */
    void setVelocity (Vector velocity) {
        this.velocity = velocity.clone();
    }

}
