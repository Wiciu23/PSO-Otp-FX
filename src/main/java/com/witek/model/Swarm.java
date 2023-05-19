package com.witek.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


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

    private int numOfParticles, epochs;
    private double inertia, cognitiveComponent, socialComponent;
    private Vector bestPosition;

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
    private OptimizationParameter[] parameters;
    private boolean isRunning = false;

    public void setFunction(OptimizationFunction function) {
        this.function = function;
    }

    /**
     * Construct the Swarm with default values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     */
    public Swarm (int functionType, int particles, int epochs) {
        this(functionType, particles, epochs, DEFAULT_INERTIA, DEFAULT_COGNITIVE, DEFAULT_SOCIAL);
    }




    /**
     * Construct the Swarm with custom values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     * @param inertia       the particles resistance to change
     * @param cognitive     the cognitive component or introversion of the particle
     * @param social        the social component or extroversion of the particle
     */
    public Swarm (int functionType, int particles, int epochs, double inertia, double cognitive, double social) {
        this.numOfParticles = particles;
        this.epochs = epochs;
        this.inertia = inertia;
        this.cognitiveComponent = cognitive;
        this.socialComponent = social;
        bestEval = Double.POSITIVE_INFINITY;
        this.parameters = OptimizeParametersFactory.getOptimizeParameters(functionType);
        this.function = OptimizeFunctionFactory.getOptimizeFunction(functionType);
    }
    public int getNumOfParticles() {
        return numOfParticles;
    }

    public void setNumOfParticles(int numOfParticles) {
        this.numOfParticles = numOfParticles;
    }

    public Vector getBestPosition() {
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

    /**
     * Execute the algorithm.
     */
    public void runOpt () {
        Particle[] particles = initialize();

        double oldEval = bestEval;
        System.out.println("--------------------------EXECUTING-------------------------");
        System.out.println("Global Best Evaluation (Epoch " + 0 + "):\t"  + bestEval + "Vec " + bestPosition.toString());

        int i = 0;
        while(bestEval > 0.000000001 && isRunning){

            if (bestEval < oldEval) {
                System.out.println("Best" + (i + 1) + "):\t" + bestEval + "Vec: " + bestPosition.toString());
                oldEval = bestEval;
                notifyBestPositionObserver();
                notifyBestEvalObserver();
               // out.write(String.valueOf(bestEval) + " \n");
            }

            for (Particle p : particles) {
                p.updatePersonalBest();
                updateGlobalBest(p);
            }

            for (Particle p : particles) {
                updateVelocity(p);
                p.updatePosition();
            }

            //System.out.println("   Epoch No: " + i + " | CURRENT ERROR: " + oldEval + "  BEST ERROR: " + bestEval + " |");
            System.out.println("   Epoch No: " + i + "= " + bestEval); //+ " VECTOR: " + bestPosition);
            i++;
            //if(bestEval < 0.0001) break;

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
        Particle[] particles = new Particle[numOfParticles];
        setInitialPosition();
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle(function,parameters);
            particles[i] = particle;
            updateGlobalBest(particle);
        }
        return particles;
    }

    private void setInitialPosition() {
        double[] initialValues = new double[vectorLength];

        Arrays.fill(initialValues, Double.POSITIVE_INFINITY);

        /*
        for (double initialValue:
             initialValues) {
             initialValue = Double.POSITIVE_INFINITY;
            //initialValue = 0.0;
        } */

        this.bestPosition = new Vector(initialValues);
    }
    //DODAĆ OBIEKT DANYCH BEZPOŚREDNIO DO ROJU.
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
        Vector oldVelocity = particle.getVelocity();
        Vector pBest = particle.getBestPosition();
        Vector gBest = bestPosition.clone();
        Vector pos = particle.getPosition();

        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        // The first product of the formula.
        Vector newVelocity = oldVelocity.clone();
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

        particle.setVelocity(newVelocity);
    }

    @Override
    public void run() {
        runOpt();
    }
}
