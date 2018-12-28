package com.github.bchapuis.markovchain4j;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NOrderMarkovChain<S> {

    public final int n;
    private final List<List<S>> inputStates;
    private final List<S> outputStates;
    private final RealMatrix probabilities;

    private NOrderMarkovChain(int n, List<List<S>> inputStates, List<S> outputStates,  RealMatrix probabilities) {
        this.n = n;
        this.inputStates = inputStates;
        this.outputStates = outputStates;
        this.probabilities = probabilities;
    }


    /**
     * Assuming that the markov chain is currently in state s, this method computes the next transitions probabilities.
     *
     * @param current the current state
     * @return the next transitions
     */
    public List<Transition<S>> next(List<S> current) {
        if (inputStates.contains(current)) {
            double[] p = probabilities.getRow(inputStates.indexOf(current));
            return outputStates.stream()
                    .map(s -> new Transition<S>(s, p[outputStates.indexOf(s)]))
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
    public double probability(List<S> current, S future, int n) {
        if (inputStates.contains(current) && outputStates.contains(future)) {
            RealMatrix pn = probabilities.power(n);
            return pn.getEntry(inputStates.indexOf(current), outputStates.indexOf(future));
        } else {
            return 0;
        }
    }

    /**
     * Creates a markov chain from a list of transitions between states.
     *
     * @param transitions the list of states
     * @param n the order of the markov chain
     * @param <S> the type of states
     * @return the markov chain
     */
    public static <S> NOrderMarkovChain<S> create(List<S> transitions, int n) {
        List<List<S>> nOrderTransitions = IntStream.range(0, transitions.size() - n + 1)
                .mapToObj(i -> transitions.subList(i, i + n)).collect(Collectors.toList());
        List<List<S>> inputStates = nOrderTransitions.stream()
                .distinct()
                .collect(Collectors.toList());
        List<Integer> inputIndexes = nOrderTransitions.stream()
                .map(state -> inputStates.indexOf(state))
                .collect(Collectors.toList());
        List<S> outputStates = transitions.stream()
                .distinct()
                .collect(Collectors.toList());
        List<Integer> outputIndexes = transitions.stream()
                .map(state -> outputStates.indexOf(state))
                .collect(Collectors.toList());
        RealMatrix probabilities = MatrixUtils.createRealMatrix(inputStates.size(), outputStates.size());
        // count transitions
        for (int i = 0; i < inputStates.size() - 1; i++) {
            double counter = probabilities.getEntry(inputIndexes.get(i), outputIndexes.get(i + n)) + 1;
            probabilities.setEntry(inputIndexes.get(i), outputIndexes.get(i + n), counter);
        }
        // compute transition probabilities
        for (int i = 0; i < inputStates.size(); i++) {
            double sum = StatUtils.sum(probabilities.getRow(i));
            if (sum != 0.0) {
                probabilities.setRowVector(i, probabilities.getRowVector(i).mapDivide(sum));
            }
        }
        return new NOrderMarkovChain<S>(n, inputStates, outputStates, probabilities);
    }

}
