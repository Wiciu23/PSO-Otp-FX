package com.witek.model;

public class Logger implements BestPositionObserver {

    private Swarm swarm;

    public Logger(Swarm swarm) {
        this.swarm = swarm;
    }

    @Override
    public void update() {
        System.out.println("LOGGER: " + swarm.getBestPosition());
    }
}
