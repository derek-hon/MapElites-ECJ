/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.mapelites;
import ec.util.*;
import ec.*;

/*
 * MapElitesBreedingSource.java
 *
 * Created: Thu Nov 18 17:40:26 1999
 * By: Sean Luke
 */

/**
 * Almost exact same as BreedingSource.java except for some parameters
 */

public abstract class MapElitesBreedingSource implements Prototype, RandomChoiceChooserD
{

    public static final String P_PROB = "prob";
    public static final double NO_PROBABILITY = -1.0;

    public double probability;

    public void setup(final EvolutionState state, final Parameter base)
    {
        Parameter def = defaultBase();

        if (!state.parameters.exists(base.push(P_PROB),def.push(P_PROB)))
            probability = NO_PROBABILITY;
        else
        {
            probability = state.parameters.getDouble(base.push(P_PROB),def.push(P_PROB),0.0);
            if (probability<0.0) state.output.error("Breeding Source's probability must be a double floating point value >= 0.0, or empty, which represents NO_PROBABILITY.",base.push(P_PROB),def.push(P_PROB));
        }
    }

    public final double getProbability(final Object obj)
    {
        return ((MapElitesBreedingSource)obj).probability;
    }

    public final void setProbability(final Object obj, final double prob)
    {
        ((MapElitesBreedingSource)obj).probability = prob;
    }

    public static int pickRandom(final MapElitesBreedingSource[] sources, final double prob)
    {
        return RandomChoice.pickFromDistribution(sources,sources[0], prob);
    }

    public static void setupProbabilities(final MapElitesBreedingSource[] sources)
    {
        RandomChoice.organizeDistribution(sources,sources[0],true);
    }

    public abstract int typicalIndsProduced();

    public Object clone() {
        try { return super.clone(); }
        catch (CloneNotSupportedException e)
        { throw new InternalError(); } // never happens
    }

    public abstract void prepareToProduce(final EvolutionState state,
                                          final int thread);

    public abstract void finishProducing(final EvolutionState s,
                                         final int thread);


    public abstract boolean produces(final EvolutionState state,
                                     final MapElitesMap newMap,
                                     int thread);

    public abstract int produce(final int min,
                                final int max,
                                final int start,
                                final Individual[] inds,
                                final EvolutionState state,
                                final int thread) ;

}
