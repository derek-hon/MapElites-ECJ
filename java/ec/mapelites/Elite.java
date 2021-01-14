package ec.mapelites;

import ec.gp.*;
import java.awt.image.BufferedImage;

/*
 * Elite.java
 *
 * Created: May 3, 2020
 * By: Derek Hon
 */

public class Elite extends GPIndividual {
    public double[] behaviourValues;
    public BufferedImage render;
    public int[] pixelValues;
    MapElitesSpecies species;

    public Object clone() {
        Elite myobj = (Elite) (super.clone());
        myobj.behaviourValues = behaviourValues;
        // myobj.render = render;
        myobj.species = species;
        myobj.pixelValues = pixelValues;
        return myobj;
    } //clone

    public Elite lightClone() {
        // a light clone
        Elite myobj = (Elite)(super.lightClone());
        myobj.behaviourValues = behaviourValues;
        // myobj.render = render;
        myobj.species = species;
        myobj.pixelValues = pixelValues;
        return myobj;
    } //lightClone
}