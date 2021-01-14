package ec.app.testmap;

import ec.util.Parameter;
import java.awt.Point;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.annotation.Target;

import ec.gp.koza.*;
import ec.mapelites.Elite;
import ec.mapelites.MapElitesProblemForm;
import ec.EvolutionState;

/**
 * DirectMatchFitness.java Created: 05/05/2020 By: Derek Hon
 **/

@SuppressWarnings("serial")
public class DirectMatchFitness extends ImageLoad implements MapElitesProblemForm {

    public BufferedImage TargetImage_ColourScheme = null;
    public DirectMatch TargetMatch = null;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        // Load targets from existing image

        int[] targetData = new int[TargetImage.getHeight() * TargetImage.getWidth()];

        for (int i = 0; i < TargetImage.getHeight() * TargetImage.getWidth(); i++)
            targetData[i] = Integer.MAX_VALUE;

        for (int i = 0; i < TargetImage.getHeight(); i++) {
            for (int j = 0; j < TargetImage.getWidth(); j++) {
                targetData[i * TargetImage.getWidth() + j] = TargetImage.getRGB(j, i);
            }
        }

        if (targetData[0] == Integer.MAX_VALUE)
            state.output.fatal("Problem " + this.getClass() + " requires a target image.");
        TargetMatch = new DirectMatch(targetData);

        state.output.systemMessage("Problem " + this.getClass() + " initialized.");
    }

    @Override
    public void evaluate(EvolutionState state, Elite ind, int threadnum) {
        super.evaluate(state, ind, threadnum);

        if (!ind.evaluated) {
            if (!(ind instanceof Elite))
                state.output.fatal("" + this.getClass() + " expects individuals of type Elite");

            // if (ind.render == null)
            // state.output.fatal("Direct match null");

            // Compute Colour distance
            DirectMatch colourDirect = new DirectMatch(ind.pixelValues);
            double[] colourDistInfo = TargetMatch.colourDirectMatch(colourDirect);
            // double[] colourDistInfo = TargetMatch.blueMatch(colourDirect);

            KozaFitness f = ((KozaFitness) ind.fitness);
            f.setStandardizedFitness(state, colourDistInfo[0]);
            f.hits = (int) colourDistInfo[1];
            ind.evaluated = true;
        } // if
    } // evaluate
}