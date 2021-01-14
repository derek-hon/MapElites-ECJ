package ec.mapelites;

import ec.util.*;
import java.io.*;
import ec.gp.GPInitializer;
import ec.*;
/*
 * MapElitesInitializer.java
 *
 * Created: 19 May, 2020
 * By: Derek Hon
 */

/**
 *
 */
public class MapElitesInitializer extends GPInitializer {
    private static final long serialVersionUID = 1;

    public static final String P_MAP = "map";

    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
    }

    public MapElitesMap initialMap(final EvolutionState state, int thread, MapElitesEvaluator evaluator) {
        MapElitesMap m = setupMap(state, thread);
        state.output.message("Populating map...");
        m.populate(state, thread, evaluator);
        return m;
    }

    public MapElitesMap setupMap(final EvolutionState state, int thread) {
        MapElitesMap m;
        Parameter base = new Parameter(P_MAP);
//        if (state.parameters.exists(base, null))
//            m = (MapElitesMap) state.parameters.getInstanceForParameterEq(base, null, MapElitesMap.class);
//        else
        m = new MapElitesMap();
        state.output.message("Setting up map...");
        m.setup(state, base);
        return m;
    }
}