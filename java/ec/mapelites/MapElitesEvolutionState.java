package ec.mapelites;

import ec.*;
import ec.util.Parameter;
import ec.util.Checkpoint;
import ec.util.Output;
import ec.simple.*;
import java.util.*;
import java.util.Random;
import ec.gp.*;
import java.util.HashMap;
import java.util.Map;

/*
 * MapElitesEvolutionState.java
 *
 * Created: May 3, 2020
 * By: Derek Hon
 */

public class MapElitesEvolutionState extends EvolutionState {

    /** How many evaluations have we run so far? */
    public long evaluations;

    /** First time calling evolve */
    protected boolean firstTime;

    /** Hash table to check for duplicate individuals */
    HashMap<Elite, Elite> individualHash;

    /** How many individuals have we added to the initial map? */
    int individualCount;

    /** The current map, not a singleton object and should only be accessed in a read only fashion. */
    public MapElitesMap map;

    public static final String P_BEHAVIOUR_SOURCE = "behaviour-source";
    BehaviourEvaluation bEvaluation;

    public static final String P_GRAPH = "do-2D-graph";
    boolean graph;

    /** Size of the map. */
    public int mapSize;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        Parameter p = new Parameter(P_BEHAVIOUR_SOURCE);
        Parameter d = new Parameter(P_BEHAVIOUR_SOURCE);

        String s = state.parameters.getString(p, d);

        if (s != null) {
            bEvaluation = (BehaviourEvaluation)(state.parameters.getInstanceForParameter (
                    p, d, BehaviourEvaluation.class
            ));
        } //if

        Boolean g = state.parameters.getBoolean(new Parameter(P_GRAPH), null, false);
        if (g != null)
            graph = g;


//        checkStatistics(state, statistics, new Parameter(P_STATISTICS));
    } //setup

//    void checkStatistics(final EvolutionState, Statistics stat, final Parameter base) {
//        if (!(stat instanceof MapElitesStatisticsForm))
//            state.output.warning("You've chosen to use Map Elites Evolution, but your statistics does not implement the MapElitesStatisticsForm.",base);
//
//        for (int i = 0 ; i < stat.children.length ; i ++) {
//            if (stat.children[x] != null)
//                checkStatistics(state, stat.children[x], base.push("child").push("" + i));
//        } //for
//    } //checkStatistics

    public void startFresh() {
        output.message("Setting up");
        setup(this, null);

        //Map Initialization
        output.message("Initializing...");
//        statistics.preInitializationStatistics(this);
        map = ((MapElitesInitializer) initializer).initialMap(this, 0, ((MapElitesEvaluator) evaluator));
        ((MapElitesStatistics)statistics).postInitializationStatistics(this);

        //Initialize variables
        firstTime = true;
        evaluations = 0;

        individualHash = new HashMap<Elite, Elite>();
    } //startFresh

//    boolean justCalledPostEvaluationStatistics = false;

    public int evolve() {
        if (evaluations % 5000 == 0) {
            output.message("Evaluations: " + evaluations);
            ((MapElitesStatistics)statistics).postEvaluationStatistics(this);
        }

        if (firstTime) {
            ((MapElitesBreeder) breeder).prepareToBreed(this, 0);
            ((MapElitesEvaluator) evaluator).prepareToEvaluate(this, 0);
            firstTime = false;
        }
        //Main evolution loop
        if (((MapElitesEvaluator) evaluator).canEvaluate()) {
            Elite ind = null;
            int retries = map.retries;

            for (int tries = 0 ; tries < retries ; tries ++) {
                ind = ((MapElitesBreeder) breeder).breedIndividual(this, 0);
                if (retries >= 1) {
                    Object o = individualHash.get(ind);
                    if (o == null) {
                        individualHash.put(ind, ind);
                        break;
                    } //if
                } //if
            } //for
            ((MapElitesEvaluator) evaluator).evaluateIndividual(this, ind);
            // if (ind.render == null) {
            //     output.message("evaluation: " + evaluations + " elite:  " + ind.toString());
            //     return R_SUCCESS;
            // }
        } //if

        Elite ind = ((MapElitesEvaluator) evaluator).getNextEvaluatedIndividual();
        //do we have an evaluated individual
        if (ind != null) {
            ind.behaviourValues = bEvaluation.individualBehaviourCalculation(this, ind, ((MapElitesEvaluator) evaluator));
            String key = map.getKey(ind.behaviourValues);
            if (evaluations != (numEvaluations - 1000)) {
                ind.render = null;
            }
            if (map.checkKey(key) && ind.fitness.betterThan(map.getElite(key).fitness)) {
                map.add(key, ind);
                individualHash.remove(map.getElite(key));
            } //if
            else if (!map.checkKey(key))
                map.add(key, ind);
            evaluations ++;
        } //if

        if (numEvaluations > UNDEFINED && evaluations >= numEvaluations) {
            output.message("Evaluations have finished");
            return R_SUCCESS;
        }
        return R_NOTDONE;
    } //evolve

    public double[] getEvaluation(EvolutionState state, Elite e, MapElitesEvaluator evaluator) {
        return bEvaluation.individualBehaviourCalculation(this, e, evaluator);
    } //get

    public void finish(int result) {
        output.message("Preparing post evolution processes...");
        for (Map.Entry<String, Elite> entry : map.map.entrySet()) {
            Elite ind = entry.getValue();
            ind.evaluated = false;
            ((MapElitesEvaluator) evaluator).prepareToEvaluate(this, 0);
            ((MapElitesEvaluator) evaluator).evaluateIndividual(this, ind);
            map.add(entry.getKey(), ind);
        }
        ((MapElitesBreeder) breeder).finishPipelines(this);
//        if (!justCalledPostEvaluationStatistics) {
//            output.message("Evaluations " + evaluations);
//            statistics.postEvaluationStatistics(this);
//        }
        ((MapElitesStatistics)statistics).finalStatistics(this,result);
    } //finish

} //MapElitesEvolutionState