package ec.mapelites;

import ec.util.*;
import java.io.*;
import java.util.*;
import ec.Setup;
import ec.EvolutionState;

/*
 * Behaviours.java
 *
 * Created: May 12, 2020
 * By: Derek Hon
 */

/**
 * Parameter example:
 * map.behaviour.0.name = "behaviour1"
 * map.behaviour.0.interval.0.ubound = 5
 * map.behaviour.0.interval.1.ubound = 10
 *
 * map behaviour.1.name = "behaviour2"
 * map.behaviour.1.interval.0.ubound = 12.5
 * map.behaviour.1.interval.1.ubound = 15.75
 * map.behaviour.1.interval.2.ubound = 25
 * map.behaviour.1.interval.3.ubound = 27.1
 * map.behaviour.1.interval.4.ubound = max
 *
 * max signifies max value for doubles
 */
public class Behaviours implements Setup, Cloneable {
    private static final long serialVersionUID = 1;

    /** The species for individuals in the behaviours. */
    public String name;

    public double[] limits;

    public static final String P_BSIZE     = "intervals";
    public static final String P_INT       = "interval";
    public static final String P_UBOUND    = "ubound";
    public static final String P_NAME      = "name";

    public Behaviours emptyClone() {
        try {
            Behaviours m = (Behaviours)clone();
            return m;
        } //try
        catch(CloneNotSupportedException e) {
            throw new InternalError();
        } //catch
    } //emptyClone

    public void clear() {
        name = "";
        for (int i = 0 ; i < limits.length ; i ++)
            limits[i] = 0;
    } //clear

    public void setup(final EvolutionState state, final Parameter base) {
        name = state.parameters.getString(base.push(P_NAME), null);
        int intervals = state.parameters.getInt(base.push(P_BSIZE), null);
        limits = new double[intervals];
        state.output.message(intervals + "");

        String inverval = "";

        for (int i = 0 ; i < intervals ; i ++) {
            String interval = state.parameters.getString(
                    base.push(P_INT).push("" + i).push(P_UBOUND), null
            );
            if (interval.equals("max"))
                limits[i] = Double.MAX_VALUE;
            else
                limits[i] = Double.parseDouble(interval);
        } //for
    } //setup

    public double[] getLimits() { return limits; }
    public double getLimit(int index) { return limits[index]; }
    public int totalLimits() { return limits.length; }
    public double getMax() {
        return limits[limits.length - 1];
    }
    public String getName() { return name; };
    public double getLimitForGraph(int index) { return limits[(limits.length - 1) - index]; }
    public int reverseOrderValue(double value) {
        for (int i = 0 ; i < limits.length ; i ++) {
            if (value == getLimit(i))
                return i;
        } //for
        return Integer.MIN_VALUE;
    } //reverseOrderValue
} //Behaviours