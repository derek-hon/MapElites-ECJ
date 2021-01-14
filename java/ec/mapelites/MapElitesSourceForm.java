

package ec.mapelites;

/*
 * MapElitesSourceForm.java
 *
 * Created: 15 May, 2020
 * By: Derek Hon
 *
 * Based off of SteadyStateBSourceForm.java
 *
 */
public interface MapElitesSourceForm
{
    /** Called whenever an individual has been replaced by another
     in the population. */
    public void individualReplaced(final MapElitesEvolutionState state,
                                   final int thread,
                                   final int individual);

    /** Issue an error (not a fatal -- we guarantee that callers
     of this method will also call exitIfErrors) if any
     of your sources, or <i>their</i> sources, etc., are not
     of SteadyStateBSourceForm.*/
    public void sourcesAreProperForm(final MapElitesEvolutionState state);
}
