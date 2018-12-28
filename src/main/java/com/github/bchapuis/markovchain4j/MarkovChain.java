package com.github.bchapuis.markovchain4j;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarkovChain<S> {

    private final List<S> states;
    private final RealMatrix probabilities;

    private MarkovChain(List<S> states, RealMatrix probabilities) {
        this.states = states;
        this.probabilities = probabilities;
    }

    /**
     * Assuming that the markov chain is currently in state s, this method computes the next transitions probabilities.
     *
     * @param current the current state
     * @return the next transitions
     */
    public List<Transition<S>> next(S current) {
        if (states.contains(current)) {
            double[] p = probabilities.getRow(states.indexOf(current));
            return states.stream()
                    .map(s -> new Transition<S>(s, p[states.indexOf(s)]))
                    .filter(s -> s.probability > 0)
                    .sorted((s1, s2) -> Double.compare(s2.probability, s1.probability))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Assuming that the markov chain is in the current state, this method computes the probability
     * that the markov chain will be in the future state after n transitions.
     *
     * @param current the current state
     * @param future the future state
     * @param n the number of transitions
     * @return the probability of the future state
     */
    public double probability(S current, S future, int n) {
        if (states.contains(current) && states.contains(future)) {
            RealMatrix pn = probabilities.power(n);
            return pn.getEntry(states.indexOf(current), states.indexOf(future));
        } else {
            return 0;
        }
    }

    /**
     * Creates a markov chain from a list of transitions between states.
     *
     * @param transitions the list of states
     * @param <S> the type of states
     * @return the markov chain
     */
    public static <S> MarkovChain<S> create(List<S> transitions) {
        List<S> states = transitions.stream()
                .distinct()
                .collect(Collectors.toList());
        List<Integer> indexes = transitions.stream()
                .map(state -> states.indexOf(state))
                .collect(Collectors.toList());
        RealMatrix probabilities = MatrixUtils.createRealMatrix(states.size(), states.size());
        // count transitions
        for (int i = 0; i < indexes.size() - 1; i++) {
            double counter = probabilities.getEntry(indexes.get(i), indexes.get(i + 1)) + 1;
            probabilities.setEntry(indexes.get(i), indexes.get(i + 1), counter);
        }
        // compute transition probabilities
        for (int i = 0; i < states.size(); i++) {
            double sum = StatUtils.sum(probabilities.getRow(i));
            if (sum != 0.0) {
                probabilities.setRowVector(i, probabilities.getRowVector(i).mapDivide(sum));
            }
        }
        return new MarkovChain<S>(states, probabilities);
    }




}
