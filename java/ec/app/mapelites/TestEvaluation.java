package ec.app.testmap;

import ec.mapelites.BehaviourEvaluation;
import java.util.*;
import ec.mapelites.Elite;
import java.awt.image.BufferedImage;
import ec.util.Parameter;
import ec.mapelites.MapElitesEvaluator;
import ec.mapelites.MapElitesEvolutionState;

/*
 * BehaviourEvaluation.java
 *
 * Created: 26 May, 2020
 * By: Derek Hon
 */

public class TestEvaluation extends BehaviourEvaluation {
    /**
     * This method is to calculate the behaviours for the individual then returns
     * these values to be used.
     *
     * @param ind Individual to calculate behaviours for
     * @return double[] behaviour values
     */
    public double[] individualBehaviourCalculation(MapElitesEvolutionState state, Elite ind,
            MapElitesEvaluator evaluator) {
        // ind.render for the bufferedImage
        double[] avgRG = new double[2];
        int red = 0, green = 0;

        Parameter p = new Parameter("map");
        int size = state.parameters.getInt(p.push("imagesize"), p.push("imagesize"), 0);
        // int[] pixels = ind.pixelValues;

        evaluator.prepareToEvaluate(state, 0);
        evaluator.evaluateIndividual(state, ind);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                red += (ind.pixelValues[i * size + j] >> 16) & 0xff;
                green += (ind.pixelValues[i * size + j] >> 8) & 0xff;
            }
        }

        avgRG[0] = red / (size * size);
        avgRG[1] = green / (size * size);

        // if (ind.render == null) {
        // evaluator.prepareToEvaluate(state, 0);
        // evaluator.evaluateIndividual(state, ind);

        // for (int i = 0 ; i < ind.render.getHeight() ; i ++) {
        // for (int j = 0 ; j < ind.render.getWidth() ; j ++) {
        // int rgb = ind.render.getRGB(j, i);
        // red += (rgb >> 16) & 0xff;
        // green += (rgb >> 8) & 0xff;
        // } //for
        // } //for
        // }
        // else {
        // for (int i = 0 ; i < ind.render.getHeight() ; i ++) {
        // for (int j = 0 ; j < ind.render.getWidth() ; j ++) {
        // int rgb = ind.render.getRGB(j, i);
        // red += (rgb >> 16) & 0xff;
        // green += (rgb >> 8) & 0xff;
        // } //for
        // } //for
        // }

        // avgRG[0] = red / (ind.render.getHeight() * ind.render.getWidth());
        // avgRG[1] = green / (ind.render.getHeight() * ind.render.getWidth());

        return avgRG;
    } // individualBehaviourCalculation

} // BehaviourEvaluation
