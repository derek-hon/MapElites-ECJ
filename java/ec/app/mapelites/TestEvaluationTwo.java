package ec.app.testmap;

import ec.mapelites.BehaviourEvaluation;
import java.util.*;
import ec.mapelites.Elite;
import java.awt.image.BufferedImage;
import ec.util.Parameter;
import ec.mapelites.MapElitesEvaluator;
import ec.mapelites.MapElitesEvolutionState;
import java.awt.Color;

/*
 * BehaviourEvaluation.java
 *
 * Created: 7 August, 2020
 * By: Derek Hon
 */

public class TestEvaluationTwo extends BehaviourEvaluation {
    /**
     * This method is to calculate the behaviours for the individual
     * then returns these values to be used.
     *
     * @param ind Individual to calculate behaviours for
     * @return double[] behaviour values
     */
    public double[] individualBehaviourCalculation(MapElitesEvolutionState state, Elite ind, MapElitesEvaluator evaluator) {
        //ind.render for the bufferedImage
        double luminance[];
        double meanLuminance = 0,
               luminanceSTD  = 0;
        Color colour;

        if (ind.render == null) {
            evaluator.prepareToEvaluate(state, 0);
            evaluator.evaluateIndividual(state, ind);

            luminance = new double[ind.render.getHeight() * ind.render.getWidth()];

            for (int i = 0 ; i < ind.render.getHeight() ; i ++) {
                for (int j = 0 ; j < ind.render.getWidth() ; j ++) {
                    int rgb = ind.render.getRGB(j, i);
                    colour = new Color(rgb);
                    luminance[i * ind.render.getWidth() + j] = (colour.getRed() * 0.3) + (colour.getGreen() * 0.59) + (colour.getBlue() * 0.11);
                    meanLuminance += luminance[i * ind.render.getWidth() + j];
                } //for
            } //for
        }
        else {
            luminance = new double[ind.render.getHeight() * ind.render.getWidth()];

            for (int i = 0 ; i < ind.render.getHeight() ; i ++) {
                for (int j = 0 ; j < ind.render.getWidth() ; j ++) {
                    int rgb = ind.render.getRGB(j, i);
                    colour = new Color(rgb);
                    luminance[i * ind.render.getWidth() + j] = (colour.getRed() * 0.3) + (colour.getGreen() * 0.59) + (colour.getBlue() * 0.11);
                    meanLuminance += luminance[i * ind.render.getWidth() + j];
                } //for
            } //for
        }

        meanLuminance /= (ind.render.getHeight() * ind.render.getWidth());

        for (double val : luminance)
            luminanceSTD += Math.pow(val - meanLuminance, 2);
        luminanceSTD = Math.sqrt(luminanceSTD / (ind.render.getHeight() * ind.render.getWidth()));

        return new double[] { meanLuminance, luminanceSTD };
    } //individualBehaviourCalculation

} //BehaviourEvaluation
