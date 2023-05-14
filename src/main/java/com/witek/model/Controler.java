package com.witek.model;

public class Controler {
    private Swarm swarm;
    private Logger logger;

    public Controler(Swarm swarm) {
        this.swarm = swarm;
        this.logger = new Logger(swarm);
        swarm.addBestPositionObserver(logger);
        swarm.run();
    }
}
