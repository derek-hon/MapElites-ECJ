/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.mapelites;
import ec.*;
import ec.util.*;
import ec.select.*;

/*
 * MapElitesRandomSelection.java
 *
 * Created: Tue Sep 3 2002
 * By: Liviu Panait
 */

/**
 * Picks a random individual in the subpopulation.  This is mostly
 * for testing purposes.
 *

 <p><b>Default Base</b><br>
 select.random

 *
 * @author Sean Luke
 * @version 1.0
 */

public class MapElitesRandomSelection extends MapElitesSelectionMethod implements MapElitesSourceForm
{
    /** default base */
    public static final String P_RANDOM = "random";

    public Parameter defaultBase()
    {
        return SelectDefaults.base().push(P_RANDOM);
    }

    public int produce(final int subpopulation,
                       final EvolutionState state,
                       final int thread) { return 0; }

    public int produce(final EvolutionState state,
                       final int thread)
    {
        return ((MapElitesEvolutionState)state).random[thread].nextInt(
                ((MapElitesEvolutionState)state).map.getSize()
        );
    }

    // I hard-code both produce(...) methods for efficiency's sake

    public int produce(final int min,
                       final int max,
                       final int start,
                       final Individual[] inds,
                       final EvolutionState state,
                       final int thread) {
        int n = 1;
        if (n>max) n = max;
        if (n<min) n = min;
        Individual[] oldinds = ((MapElitesEvolutionState)state).map.asIndArray();

        for(int q = 0; q < n; q++)
            inds[start+q] = oldinds[state.random[thread].nextInt( ((MapElitesEvolutionState)state).map.getSize() )];
        return n;
    }

    public void individualReplaced(final MapElitesEvolutionState state,
                                   final int thread,
                                   final int individual) { return; }

    public void sourcesAreProperForm(final MapElitesEvolutionState state) { return; }

    public boolean produces(final EvolutionState state,
                            final MapElitesMap newmap,
                            final int thread) {
        return true;
    }

    public void prepareToProduce(final EvolutionState s,
                                 final int thread)
    { return; }

    public void finishProducing(final EvolutionState s,
                                final int thread)
    { return; }

}
