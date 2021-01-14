/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/



package ec.mapelites;

import ec.*;
import ec.util.*;
import java.util.*;
import ec.mapelites.Elite;
import ec.eval.MasterProblem;

/*
 * MapElitesEvaluator.java
 *
 * Created: 15 May, 2020
 * By: Derek Hon
 */

public class MapElitesEvaluator extends Evaluator
{
    LinkedList<QueueIndividual> queue = new LinkedList<QueueIndividual>();
    MapElitesProblemForm problem;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        if (!(p_problem instanceof MapElitesProblemForm))
            state.output.fatal("" + this.getClass() + " used, but the Problem is not of MapElitesProblemForm",
                    base.push(P_PROBLEM));
    }

    public void prepareToEvaluate(EvolutionState state, int thread) {
        problem = (MapElitesProblemForm) p_problem.clone();

        /*
           We only call prepareToEvaluate during Asynchronous Evolution.
        */
        if (problem instanceof MasterProblem)
            ((MasterProblem)problem).prepareToEvaluate(state, thread);
    }

    /** Submits an individual to be evaluated by the Problem, and adds it is added to the queue. */
    public void evaluateIndividual(final EvolutionState state, Elite ind) {
        prepareToEvaluate(state, 0);
        problem.evaluate(state, ind, 0);
        queue.addLast(new QueueIndividual(ind));
    }

    /** Returns true if we're ready to evaluate an individual.  Ordinarily this is ALWAYS true,
     except in the asynchronous evolution situation, where we may not have a processor ready yet. */
    public boolean canEvaluate() {
        if (problem instanceof MasterProblem)
            return ((MasterProblem)problem).canEvaluate();
        else return true;
    }

    /** Returns an evaluated individual is in the queue and ready to come back to us.
     Ordinarily this is ALWAYS true at the point that we call it, except in the asynchronous
     evolution situation, where we may not have a job completed yet, in which case NULL is
     returned. Once an individual is returned by this function, no other individual will
     be returned until the system is ready to provide us with another one.  NULL will
     be returned otherwise.  */
    public Elite getNextEvaluatedIndividual() {
        QueueIndividual qind = null;
        qind = (QueueIndividual)(queue.removeFirst());

        if (qind == null) return null;

        return qind.ind;
    }

    /** Function here to satisfy abstract class */
    public void evaluatePopulation(final EvolutionState state) {}

    /** Run will only be complete after evaluation limit has been reached. */
    public String runComplete(final EvolutionState state) { return runComplete; } //runComplete
} //MapElitesEvaluator


