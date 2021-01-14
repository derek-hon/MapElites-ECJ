package ec.mapelites;

import ec.util.*;
import java.io.*;
import java.util.*;
import ec.EvolutionState;
import ec.Individual;
import ec.Setup;
import ec.ECDefaults;

/*
 * MapElitesMap.java
 *
 * Created: May 6, 2020
 * By: Derek Hon
 */

/**
 * Parameter example:
 * map.behaviours = 2
 * map.behaviour.0.intervals = 2
 * map.behaviour.1.intervals = 5
 * map.startInds = 8
 */
public class MapElitesMap implements Setup, Cloneable {
    private static final long serialVersionUID = 1;

    public Behaviours[] behaviours;

    public static final String P_MAP        = "map";
    public static final String P_SIZE       = "behaviours";
    public static final String P_BEHAVIOUR  = "behaviour";
    public static final String P_FILE       = "file";
    public static final String P_SPECIES    = "species";
    public static final String P_RETRIES    = "duplicate-retries";
    public static final String P_START_INDS = "startInds";

    public int startSize;

    public static final String NUM_INDIVIDUALS_PREAMBLE = "Number of Individuals: ";
    public static final String INDIVIDUAL_INDEX_PREAMBLE = "Individual Number: ";

    /** Number of retries for duplicates */
    public int retries;

    /** Species for the map */
    public MapElitesSpecies species;

    /** The Maps individuals with the key being the behaviour and behaviour value,
     *  so each individual value has its key of x behaviours */
    public HashMap<String, Elite> map;
    /* A new Map should be loaded from this resource name if it is non-null;
           otherwise they should be created at random.  */
    public boolean loadInds;
    public Parameter file;

    public Parameter defaultBase()
    {
        return ECDefaults.base().push(P_BEHAVIOUR);
    }

    public MapElitesMap emptyClone() {
        try {
            MapElitesMap m = (MapElitesMap)clone();
            return m;
        }
        catch(CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public void clear() {
        map.clear();
    }

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter inds = base.push(P_START_INDS);
        startSize = state.parameters.getInt(inds, null, 1);

        map = new HashMap<String, Elite>();

        //Load from file?
        file = base.push(P_FILE);
        loadInds = state.parameters.exists(file, null);

        //Number of behaviours
        Parameter size = base.push(P_SIZE);
        int numBehaviours = state.parameters.getInt(size, null, 1);
        if (numBehaviours == 0)
            state.output.fatal("Number of behaviours must be greater than 0.\n", base.push(P_SIZE));

        behaviours = new Behaviours[numBehaviours];

        //species
        species = (MapElitesSpecies) state.parameters.getInstanceForParameter(
                base.push(P_SPECIES), null, MapElitesSpecies.class
        );
        species.setup(state, base.push(P_SPECIES));

        //Set up behaviour limits
        for (int i = 0 ; i < numBehaviours ; i ++) {
            //behaviour parameter
            Parameter p = base.push(P_BEHAVIOUR).push("" + i);
            if (!state.parameters.exists(p, null)) {
                int defaultBehaviour = state.parameters.getInt(p, null, 0);
                if (defaultBehaviour >= 0) {
                    state.output.warning("Using behaviour " + defaultBehaviour + " as default for behaviour " + i);
                    p = base.push(P_BEHAVIOUR).push("" + defaultBehaviour);
                }
            }
            //Behaviour handling
            Parameter def = defaultBase();

            int intervals;

            intervals = state.parameters.getInt(p.push(Behaviours.P_BSIZE), null, 1);
            if (intervals <= 0) {
                state.output.fatal(
                        "Number of intervals must be >= 1.\n",
                        p.push(Behaviours.P_BSIZE), def.push(Behaviours.P_BSIZE)
                );
            }

            //Retry when duplicate found
            retries = state.parameters.getInt(base.push(P_RETRIES), null, 0);

            if (retries < 0)
                state.output.fatal(
                        "Number of retries for duplicate entries must be an integer >= 0.\n",
                        base.push(P_RETRIES), def.push(P_RETRIES)
                );

            behaviours[i] = (Behaviours)(state.parameters.getInstanceForParameterEq(p,null,Behaviours.class));
            behaviours[i].setup(state, p);
        } //for loop
    } //setup

    public void populate(EvolutionState state, int thread, MapElitesEvaluator evaluator) {
        for (int i = 0 ; i < startSize ; i ++) {
            for (int j = 0 ; j < retries ; j ++) {
                Elite e = species.newIndividual(state, thread);
                e.behaviourValues = ((MapElitesEvolutionState)state).getEvaluation(state, e, evaluator);

                String key = getKey(e.behaviourValues);
                if (!map.containsKey(key)) {
                    map.put(key, e);
                    break;
                } //if
            } //if
        } //for
        state.output.message("Populated.");
    } //populate

    public Individual[] asIndArray() {
        Elite[] old = asArray();
        Individual[] newInd = new Individual[old.length];

        for (int i = 0 ; i < old.length ; i ++)
            newInd[i] = (Individual)old[i];

        return newInd;
    } //asIndArray

    public Elite[] asArray() {
        Collection<Elite> values = map.values();
        Elite[] eliteArr = values.toArray(new Elite[values.size()]);

        return eliteArr;
    } //asArray

    public void add(String key, Elite e) {
        map.put(key, e);
    } //add

    public Elite getElite(String key) {
        return map.get(key);
    } //getElite

    public int getSize() {
        return map.size();
    } //getSize

    public boolean checkKey(String key) {
        return map.containsKey(key);
    } //checkKey

    public String getKey(double[] values) {
        String key = "";
        for (int i = 0 ; i < behaviours.length ; i ++) {
            key += behaviours[i].name;
            for (double val : behaviours[i].limits) {
                if(values[i] <= val) {
                    key += "" + val;
                    break;
                } //if
            } //for
        } //for
        return key;
    } //getKey
} //Map