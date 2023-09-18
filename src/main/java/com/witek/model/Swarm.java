package com.witek.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;


/**`
 * Represents a swarm of particles from the Particle Swarm Optimization algorithm.
 */
public class Swarm implements SwarmInterface, Runnable {
    
    //add Observable interface related to each of dynamic parameters 
    // - bestEval, bestPosition, currentPositions and currentEval (wtedy musiałoby być kilka ? i na żywo wyświetlane ? )
    // zapytac chatgpt o pomysły na zwizualizowanie 12 wymiarowego vektora + plan plasy magisterskiej
    // Przedstawić rezultat na "żywo" z historią jako 2d t-sne algorytmu 
    // Napisanie statycznej metody fabrykującej dla Cząsteczki

    //Observers
    ArrayList<SwarmParamsObserver> swarmParamsObservers = new ArrayList<>();
    ArrayList<BestPositionObserver> bestPositionObservers = new ArrayList<>();
    ArrayList<BestEvalObserver> bestEvalObservers = new ArrayList<>();
    ArrayList<BestEvalObserver> optimizationParametersObservers = new ArrayList<>();

    private int numOfParticles, epochs;
    private double inertia, cognitiveComponent, socialComponent;
    private VectorOperations bestPosition;

    public double getBestEval() {
        return bestEval;
    }

    public void setBestEval(double bestEval) {
        this.bestEval = bestEval;
    }

    private double bestEval;
    public static final double DEFAULT_INERTIA = 0.729844;
    public static final double DEFAULT_COGNITIVE = 1.496180; // Cognitive component. == Social component
    public static final double DEFAULT_SOCIAL = 1.496180; // Social component. 1,496180
    int vectorLength;
    private OptimizationFunction function;

    public OptimizationParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(OptimizationParameter[] parameters) {
        this.parameters = parameters;
    }

    private OptimizationParameter[] parameters;
    private boolean isRunning = false;
    Particle[] particles;

    public void setFunction(OptimizationFunction function) {
        this.function = function;
    }

    /**
     * Construct the Swarm with default values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     */
    public Swarm (OptimizationParameter[] parameters, OptimizationFunction objFunction, int particles, int epochs) {
        this(parameters, objFunction, particles, epochs, DEFAULT_INERTIA, DEFAULT_COGNITIVE, DEFAULT_SOCIAL);
    }




    /**
     * Construct the Swarm with custom values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     * @param inertia       the particles resistance to change
     * @param cognitive     the cognitive component or introversion of the particle
     * @param social        the social component or extroversion of the particle
     */
    public Swarm (OptimizationParameter[] parameters,OptimizationFunction objFunction, int particles, int epochs, double inertia, double cognitive, double social) {
        this.numOfParticles = particles;
        this.epochs = epochs;
        this.inertia = inertia;
        this.cognitiveComponent = cognitive;
        this.socialComponent = social;
        bestEval = Double.POSITIVE_INFINITY;
        this.vectorLength = parameters.length;
        double[] initialValuesPosition = new double[vectorLength];
        Arrays.fill(initialValuesPosition, Double.POSITIVE_INFINITY);
        this.parameters = parameters;
        this.bestPosition = new VectorLockable(new Vector(initialValuesPosition),parameters);
        this.function = objFunction;
    }
    public int getNumOfParticles() {
        return numOfParticles;
    }

    public void setNumOfParticles(int numOfParticles) {
        this.numOfParticles = numOfParticles;
    }

    public VectorOperations getBestPosition() {
        return bestPosition;
    }

    public double getInertia() {
        return inertia;
    }

    public double getCognitiveComponent() {
        return cognitiveComponent;
    }

    public double getSocialComponent() {
        return socialComponent;
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
    }

    public void setCognitiveComponent(double cognitiveComponent) {
        this.cognitiveComponent = cognitiveComponent;
    }

    public void setSocialComponent(double socialComponent) {
        this.socialComponent = socialComponent;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void addBestPositionObserver(BestPositionObserver o) {
        bestPositionObservers.add(o);
    }

    @Override
    public void addBestEvalObserver(BestEvalObserver o) {
        bestEvalObservers.add(o);
    }

    @Override
    public void addSwarmParamsObserver(SwarmParamsObserver o) {
        swarmParamsObservers.add(o);
    }
    //

    private void notifyBestPositionObserver(){
        for (BestPositionObserver observer:
             bestPositionObservers) {
            observer.update();
        }
    }

    private void notifyBestEvalObserver(){
        for (BestEvalObserver observer:
                bestEvalObservers) {
            observer.update();
        }
    }

    public void initializeSwarm(){
        this.particles = initialize();
    }

    public void reInitializeSwarm(){

    }

    /**
     * Execute the algorithm.
     */
    public void runOpt () {
        double oldEval = bestEval;
        System.out.println("--------------------------EXECUTING-------------------------");
        System.out.println("Global Best Evaluation (Epoch " + 0 + "):\t"  + bestEval + " Vec " + bestPosition.toString());
        notifyBestPositionObserver();
        int i = 0;
        while(bestEval > 0.000000000000001 && isRunning){

            if (bestEval < oldEval) {
                System.out.println("Best" + (i + 1) + "):\t" + bestEval + "Vec: " + bestPosition.toString());
                oldEval = bestEval;
                notifyBestPositionObserver();
                notifyBestEvalObserver();
            }

            for (Particle p : particles) {
                p.updatePersonalBest();
                updateGlobalBest(p);
            }

            for (Particle p : particles) {
                updateVelocity(p);
                p.updatePosition();
            }

            System.out.println("   Epoch No: " + i + "= " + bestEval + " POSITION: " + bestPosition); //+ " VECTOR: " + bestPosition);
            i++;
        }

        System.out.println("---------------------------RESULT---------------------------");
        System.out.println("a = " + bestPosition.toString());
        System.out.println("Final Best Evaluation: " + bestEval);
        System.out.println("---------------------------COMPLETE-------------------------");

      /*  out.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } */

    }

    /**
     * Create a set of particles, each with random starting positions.
     * @return  an array of particles
     */
    private Particle[] initialize () {
        setInitialPosition();
        Particle[] particles = new Particle[numOfParticles];
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle(function,parameters);
            particles[i] = particle;
            updateGlobalBest(particle);
        }
        return particles;
    }

    private void setInitialPosition() {
        double[] initialValues = new double[vectorLength];

        for (int i = 0 ; i < initialValues.length ; i++){
            initialValues[i] = rand(parameters[i].getLowerBound(),parameters[i].getUpperBound());
        }
        //Arrays.fill(initialValues, Double.POSITIVE_INFINITY);

        /*
        for (double initialValue:
             initialValues) {
             initialValue = Double.POSITIVE_INFINITY;
            //initialValue = 0.0;
        } */
        Vector vector = new Vector(initialValues);
        this.bestPosition = new VectorLockable(vector,parameters);
    }

    private static double rand (double beginRange, double endRange) {
        Random r = new Random();
        return beginRange + (r.nextDouble()*(endRange-beginRange));
    }

    /**
     * Update the global best solution if a the specified particle has
     * a better solution
     * @param particle  the particle to analyze
     */
    private void updateGlobalBest (Particle particle) {
        if (particle.getBestEval() < bestEval) {
            bestPosition = particle.getBestPosition();
            bestEval = particle.getBestEval();
        }
    }

    /**
     * Update the velocity of a particle using the velocity update formula
     * @param particle  the particle to update
     */

    private void updateVelocity (Particle particle) {
        VectorOperations oldVelocity = particle.getVelocity();
        VectorOperations pBest = particle.getBestPosition();
        VectorOperations gBest = bestPosition.clone();
        VectorOperations pos = particle.getPosition();

        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        // The first product of the formula.
        VectorOperations newVelocity = oldVelocity.clone();
        newVelocity.mul(inertia);

        // The second product of the formula.
        pBest.sub(pos);
        pBest.mul(cognitiveComponent);
        pBest.mul(r1);
        newVelocity.add(pBest);

        // The third product of the formula.
        gBest.sub(pos);
        gBest.mul(socialComponent);
        gBest.mul(r2);
        newVelocity.add(gBest);
        //checkBoundaries(newVelocity); //check velocity if its out of boundaries
        particle.setVelocity(newVelocity);
    }

    private void checkBoundaries(VectorOperations vector) {
        double[] coordinates = vector.getCordinates();
        for(int i = 0 ; i < coordinates.length ; i++){
            coordinates[i] = min(max(coordinates[i],parameters[i].getLowerBound()),parameters[i].getUpperBound());
        }
    }

    @Override
    public void run() {
        runOpt();
    }
}
