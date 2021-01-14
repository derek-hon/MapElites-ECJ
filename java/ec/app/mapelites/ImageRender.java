/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.app.testmap;

import static ec.app.testmap.CoordinateVariables.*;

import java.awt.image.BufferedImage;
import java.util.*;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPInitializer;
import ec.mapelites.MapElitesProblem;
import ec.util.Parameter;

import ec.mapelites.Elite;

import ec.mapelites.MapElitesProblemForm;

/*
 * ImageRender.java
 *
 * Created: 29 May, 2020
 * By: Michael Gircys, Derek Hon
 */

public class ImageRender extends MapElitesProblem implements TextureProblemForm {
    protected static int Default_Image_Size = 256;
    private double[] Current_Pos = new double[CoordinateVariables.values().length];

    private static final String P_MIN_X = "min_x";
    private static final String P_MAX_X = "max_x";
    private static final String P_MIN_Y = "min_y";
    private static final String P_MAX_Y = "max_y";

    private double yMin = 0, yMax = 0, xMin = 0, xMax = 0;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        Parameter def = base;

        xMin = state.parameters.getDouble(base.push(P_MIN_X), def.push(P_MIN_X));
        xMax = state.parameters.getDouble(base.push(P_MAX_X), def.push(P_MAX_X));
        yMin = state.parameters.getDouble(base.push(P_MIN_Y), def.push(P_MIN_Y));
        yMax = state.parameters.getDouble(base.push(P_MAX_Y), def.push(P_MAX_Y));

        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof MultiData))
            state.output.fatal("GPData class must subclass from " + MultiData.class, base.push(P_DATA), null);
    }

    @Override
    public void evaluate(final EvolutionState state, final Elite ind, final int threadnum) {
        // if (ind.evaluated) return;

        // renders an image each time which consumes too much memory
        // ind.render = RenderImage(state, ind, threadnum, Default_Image_Size);

        ind.pixelValues = RenderColour(state, ind, threadnum, Default_Image_Size);

        // if (ind.render == null)
        // state.output.fatal("here");
    } // evaluate

    // We expect a [0.0,1.0] range. Make it [0,255] (fairly).
    private int FitToChannelRange(double v) {
        v = Math.min(Math.max(0.0, v), 1.0);
        return (int) Math.ceil(v * 255.0);
    }

    public int[] RenderColour(EvolutionState state, Elite ind, int threadnum, int size) {
        final int Image_Size_X = size;
        final int Image_Size_Y = size;

        MultiData input = (MultiData) this.input;

        double xIncrement = (xMax - xMin) / 256;
        double yIncrement = (yMax - yMin) / 256;

        double textureY = yMin;
        double textureX = 0;

        int colours[] = new int[size * size];
        for (int i = 0; i < size * size; i++)
            colours[i] = Integer.MIN_VALUE;

        for (int y = 0; y < Image_Size_Y; y++) {
            textureX = xMin;
            for (int x = 0; x < Image_Size_X; x++) {
                int r, g, b, rgb = 0;

                Current_Pos[X.ordinal()] = textureX;
                Current_Pos[Y.ordinal()] = textureY;

                // Multiple colour channels.
                ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                r = FitToChannelRange(input.GetD());
                ind.trees[1].child.eval(state, threadnum, input, stack, ind, this);
                g = FitToChannelRange(input.GetD());
                ind.trees[2].child.eval(state, threadnum, input, stack, ind, this);
                b = FitToChannelRange(input.GetD());
                rgb = (r << 16) + (g << 8) + (b);

                colours[y * Image_Size_X + x] = rgb;
                textureX += xIncrement;
            } // end for
            textureY += yIncrement;
        } // end for

        for (int i = 0; i < size * size; i++)
            if (colours[i] == Integer.MIN_VALUE)
                state.output.fatal("colour not set");
        return colours;
    }

    // public BufferedImage RenderImage(EvolutionState state, Elite ind, int
    // threadnum, int size) {
    // final int Image_Size_X = size;
    // final int Image_Size_Y = size;

    // MultiData input = (MultiData)this.input;

    // double xIncrement = (xMax - xMin)/256;
    // double yIncrement = (yMax - yMin)/256;

    // double textureY = yMin;
    // double textureX = 0;

    // BufferedImage outImage = new BufferedImage(Image_Size_X, Image_Size_Y,
    // BufferedImage.TYPE_INT_RGB);

    // for(int y = 0; y < Image_Size_Y; y++) {
    // textureX = xMin;
    // for (int x = 0; x < Image_Size_X; x++) {
    // int r, g, b, rgb = 0;

    // Current_Pos[X.ordinal()] = textureX;
    // Current_Pos[Y.ordinal()] = textureY;

    // // Multiple colour channels.
    // ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
    // r = FitToChannelRange(input.GetD());
    // ind.trees[1].child.eval(state, threadnum, input, stack, ind, this);
    // g = FitToChannelRange(input.GetD());
    // ind.trees[2].child.eval(state, threadnum, input, stack, ind, this);
    // b = FitToChannelRange(input.GetD());
    // rgb = (r << 16) + (g << 8) + (b);

    // outImage.setRGB(x, y, rgb);
    // textureX += xIncrement;
    // } //end for
    // textureY += yIncrement;
    // } //end for

    // if (outImage == null)
    // state.output.fatal("imagerender null");
    // return outImage;
    // }

    public double[] Get_Current_Pos() {
        return Current_Pos;
    }

    public void Set_Current_Pos(double[] p) {
        Current_Pos = p;
    }

    public void describe(final EvolutionState state, final Elite ind, final int threadnum, final int log) {
    }

    @Override
    public Object clone() {
        ImageRender o = (ImageRender) super.clone();
        return o;
    }
}
