package ec.mapelites;

import ec.*;
import ec.util.Parameter;
import ec.util.*;

/*
 * MapElitesEvolutionState.java
 *
 * Created: May 3, 2020
 * By: Derek Hon
 */

public class MapElitesBreeder extends Breeder {

    private static final long serialVersionUID = 1;

    /**  */
    MapElitesBreedingPipeline bp;

    public ThreadPool pool = new ThreadPool();

    public MapElitesBreeder() {
        bp = null;
    }

    public void setup (final EvolutionState state, final Parameter base) { }

    /** Called to check to see if the breeding sources are correct -- if you
     use this method, you must call state.output.exitIfErrors() immediately
     afterwards. */
//    public void sourcesAreProperForm(final MapElitesEvolutionState state,
//                                     final MapElitesBreedingPipeline[] breedingPipelines) {
//        // all breeding pipelines are MapElitesSourceForm
//        for(int i = 0 ; i < breedingPipelines.length ; i ++)
//            breedingPipelines[i].sourcesAreProperForm(state);
//    }

    /** Unused function but required for abstract cass */
    public void individualReplaced(final MapElitesEvolutionState state,
                                   final int thread,
                                   final int individual) { }

    public void finishPipelines(EvolutionState state) {
        bp.finishProducing(state, 0);
    }

    public void prepareToBreed(EvolutionState state, int thread)
    {
        final MapElitesEvolutionState st = (MapElitesEvolutionState) state;
        // set up the breeding pipelines

        bp = (MapElitesBreedingPipeline)st.map.species.pipe_prototype;
        if (!bp.produces(st,st.map,0))
            st.output.error("The Map Elites Breeding Pipeline does not produce individuals of the expected species " + st.map.species.getClass().getName() + " and with the expected Fitness class " + st.map.species.f_prototype.getClass().getName());
        // are they of the proper form?
//        sourcesAreProperForm(st,bp);
        // because I promised when calling sourcesAreProperForm
        st.output.exitIfErrors();

        // warm them up
        bp.prepareToProduce(state, 0);
    }

    public Elite breedIndividual(final EvolutionState state, int thread)
    {
        Individual[] newind = new Individual[1];

        // breed a single individual
        bp.produce(1, 1, 0,newind, state, thread);
        return (Elite)newind[0];
    }

    /** Empty because we have no need for it */
    public Population breedPopulation(EvolutionState state) { return null; }

}

