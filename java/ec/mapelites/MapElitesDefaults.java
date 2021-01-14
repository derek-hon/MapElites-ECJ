package ec.mapelites;

import ec.*;
import ec.util.*;

/*
 * MapElitesDefaults.java
 *
 * Created: May 14, 2020
 * By: Derek Hon
 */


public final class MapElitesDefaults implements DefaultsForm
{
    public static final String P_MAPELITES = "mapelites";

    /** Returns the default base. */
    public static final Parameter base()
    {
        return new Parameter(P_MAPELITES);
    }
}
