package com.witek.model;

public interface SwarmInterface {
    void addBestPositionObserver(BestPositionObserver o);

    void addBestEvalObserver(BestEvalObserver o);

    void addSwarmParamsObserver(SwarmParamsObserver o);

    void run();

    Vector getBestPosition();

}
