package com.github.bchapuis.markovchain4j;

public class Transition<S> {

    public final S state;
    public final double probability;

    public Transition(S state, double p) {
        this.state = state;
        this.probability = p;
    }

}
