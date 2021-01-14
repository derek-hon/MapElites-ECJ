/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.mapelites;
import ec.*;
import ec.util.*;
import ec.breed.*;
import java.lang.StackTraceElement;


/*
 * MultiBreedingPipeline.java
 *
 * Created: 16 July, 2020
 * By: Sean Luke and Derek Hon
 */

/**
 * Based off of MultiBreedingPipeline
 *
 * @see ec.breed.MultiBreedingPipeline
 */
public class MapElitesMultiBreedingPipeline extends MapElitesBreedingPipeline
{
    public static final String P_GEN_MAX = "generate-max";
    public static final String P_MULTIBREED = "multibreed";

    public int maxGeneratable;
    public boolean generateMax;

    public Parameter defaultBase()
    {
        return BreedDefaults.base().push(P_MULTIBREED);
    }

    public int numSources() { return DYNAMIC_SOURCES; }

    public void setup(final EvolutionState state, final Parameter base)
    {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
        super.setup(state,base);

        Parameter def = defaultBase();

        double total = 0.0;

        if (sources.length == 0)  // uh oh
            state.output.fatal("num-sources must be provided and > 0 for MultiBreedingPipeline",
                    base.push(P_NUMSOURCES), def.push(P_NUMSOURCES));

        for(int x=0;x<sources.length;x++)
        {
            // make sure the sources are actually breeding pipelines
            if (!(sources[x] instanceof MapElitesBreedingPipeline))
                state.output.error("Source #" + x + "is not a MapElitesBreedingPipeline",base);
            else if (sources[x].probability<0.0) // null checked from state.output.error above
                state.output.error("Pipe #" + x + " must have a probability >= 0.0",base);  // convenient that NO_PROBABILITY is -1...
            else total += sources[x].probability;
        }

        state.output.exitIfErrors();

        // Now check for nonzero probability (we know it's positive)
        if (total == 0.0)
            state.output.warning("MapElitesMultiBreedingPipeline's children have all zero probabilities -- this will be treated as a uniform distribution.  This could be an error.", base);

        // allow all zero probabilities
        MapElitesBreedingSource.setupProbabilities(sources);

        generateMax = state.parameters.getBoolean(base.push(P_GEN_MAX),def.push(P_GEN_MAX),true);
        maxGeneratable=0;  // indicates that I don't know what it is yet.

        // declare that likelihood isn't used
        if (likelihood < 1.0)
            state.output.warning("MapElitesMultiBreedingPipeline does not respond to the 'likelihood' parameter.",
                    base.push(P_LIKELIHOOD), def.push(P_LIKELIHOOD));
    }

    /** Returns the max of typicalIndsProduced() of all its children */
    public int typicalIndsProduced()
    {
        if (maxGeneratable==0) // not determined yet
            maxGeneratable = maxChildProduction();
        return maxGeneratable;
    }


    public int produce(final int min,
                       final int max,
                       final int start,
                       final Individual[] inds,
                       final EvolutionState state,
                       final int thread)

    {
        MapElitesBreedingSource s = sources[MapElitesBreedingSource.pickRandom(
                sources,state.random[thread].nextDouble())];
        int total;

        total = s.produce(
                    min,max,start,inds,state,thread);

        // clone if necessary
        if (s instanceof MapElitesSelectionMethod)
            for(int q=start; q < total+start; q++)
                inds[q] = (Individual)(inds[q].clone());

        return total;
    }
}
