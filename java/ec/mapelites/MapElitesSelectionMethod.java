/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.mapelites;

import ec.*;

/*
 * MapElitesSelectionMethod.java
 *
 * Created: 17 July, 2020
 * By: Sean Luke and Derek Hon
 */

/**
 * Based off of SelectionMethod
 *
 * @see ec.SelectionMethod
 */
public abstract class MapElitesSelectionMethod extends MapElitesBreedingSource
{
    public static final int INDS_PRODUCED = 1;

    /** Returns 1 (the typical default value) */
    public int typicalIndsProduced() { return INDS_PRODUCED; }

    /** A default version of produces -- this method always returns
     true under the assumption that the selection method works
     with all Fitnesses.  If this isn't the case, you should override
     this to return your own assessment. */
    public boolean produces(final EvolutionState state,
                            final MapElitesMap newmap,
                            final int thread) {
        return true;
    }


    /** A default version of prepareToProduce which does nothing.  */
    public void prepareToProduce(final EvolutionState s,
                                 final int thread)
    { return; }

    /** A default version of finishProducing, which does nothing. */
    public void finishProducing(final EvolutionState s,
                                final int thread)
    { return; }

    public int produce(final int min,
                       final int max,
                       final int start,
                       final Individual[] inds,
                       final EvolutionState state,
                       final int thread)
    {
        int n=INDS_PRODUCED;
        if (n<min) n = min;
        if (n>max) n = max;
        Individual[] oldinds = ((MapElitesEvolutionState)state).map.asIndArray();

        for(int q=0;q<n;q++)
            inds[start+q] = oldinds[produce(state,thread)];
        return n;
    }

    /** An alternative form of "produce" special to Selection Methods;
     selects an individual from the given subpopulation and
     returns its position in that subpopulation. */
    public abstract int produce(final EvolutionState state,
                                final int thread);
}
