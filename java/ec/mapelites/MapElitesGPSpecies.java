/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.mapelites;
import ec.*;
import ec.util.*;
import java.io.*;
import ec.gp.*;
import java.lang.StackTraceElement;

/*
 * MapElitesGPSpecies.java
 *
 * Created: 16 July, 2020
 * By: Sean Luke and Derek Hon
 */

/**
 * Based off of GPSpecies
 *
 * @see ec.gp.GPSpecies
 */

public class MapElitesGPSpecies extends MapElitesSpecies
{
    public static final String P_MEGPSPECIES = "species";

    public Parameter defaultBase()
    {
        return GPDefaults.base().push(P_MEGPSPECIES);
    }

    public void setup(final EvolutionState state, final Parameter base)
    {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected

        super.setup(state,base);

        // check to make sure that our individual prototype is a Elite
        if (!(i_prototype instanceof Elite))
            state.output.fatal("The Individual class for the Species " + getClass().getName() + " is must be a subclass of ec.mapelites.Elite.", base );
    }

    public Elite newIndividual(EvolutionState state, int thread)
    {
        Elite newind = i_prototype.lightClone();

        // Initialize the trees
        for (int x=0;x<newind.trees.length;x++)
            newind.trees[x].buildTree(state, thread);

        // Set the fitness
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false;

        // Set the species to me
        newind.species = this;

        // ...and we're ready!
        return newind;
    }


    // A custom version of newIndividual() which guarantees that the
    // prototype is light-cloned before readIndividual is issued
    public Elite newIndividual(final EvolutionState state,
                                    final LineNumberReader reader)
            throws IOException
    {
        Elite newind = i_prototype.lightClone();

        // Set the fitness -- must be done BEFORE loading!
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // load that sucker
        newind.readIndividual(state,reader);

        // Set the species to me
        newind.species = this;

        // and we're ready!
        return newind;
    }


    // A custom version of newIndividual() which guarantees that the
    // prototype is light-cloned before readIndividual is issued
    public Elite newIndividual(final EvolutionState state,
                                    final DataInput dataInput)
            throws IOException
    {
        Elite newind = i_prototype.lightClone();

        // Set the fitness -- must be done BEFORE loading!
        newind.fitness = (Fitness)(f_prototype.clone());
        newind.evaluated = false; // for sanity's sake, though it's a useless line

        // Set the species to me
        newind.species = this;

        // load that sucker
        newind.readIndividual(state,dataInput);

        // and we're ready!
        return newind;
    }

}
