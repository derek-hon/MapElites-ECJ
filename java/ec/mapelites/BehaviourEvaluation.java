package ec.mapelites;

import ec.EvolutionState;

/*
 * BehaviourEvaluation.java
 *
 * Created: 26 May, 2020
 * By: Derek Hon
 */

public abstract class BehaviourEvaluation {
    /**
     * This method is to calculate the behaviours for the individual
     * then returns these values to be used.
     *
     * @param ind Individual to calculate behaviours for
     * @return double[] behaviour values
     */
    public abstract double[] individualBehaviourCalculation(MapElitesEvolutionState state, Elite ind, MapElitesEvaluator evaluator);
} //BehaviourEvaluation
