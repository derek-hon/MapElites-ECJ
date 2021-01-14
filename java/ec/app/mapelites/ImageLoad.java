package ec.app.testmap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import ec.mapelites.*;
import ec.EvolutionState;
import ec.util.Parameter;
import java.nio.file.*;


@SuppressWarnings("serial")
public abstract class ImageLoad extends ImageRender implements MapElitesProblemForm {

    static final String P_TARGET = "target";

    // PSD library is expecting a square image (double[][]) with specific size.
    // protected static int imageSize = 256;

    public BufferedImage TargetImage      = null;
    public double[][]    TargetImage_Gray = null;

    @Override
    public void setup(final EvolutionState state, final Parameter base)
    {
        super.setup(state, base);

        // Default_Image_Size = imageSize;
        Parameter p = new Parameter("map");
        int size = state.parameters.getInt(p.push("imagesize"), p.push("imagesize"), 0);
        state.output.message("size: " + size);
        state.output.message("Obtaining target image...");
        // Load targets from existing image
        if( state.parameters.exists(base.push("target"),null) ) {
            // Load Image
            File targetFile = state.parameters.getFile(base.push(P_TARGET),null);
            // TargetImage = ImageIO.read(targetFile);
            state.output.message(targetFile.toPath().toString());
            try {
                // File targetFile = state.parameters.getFile(base.push(P_TARGET),null);
                TargetImage = ImageIO.read(targetFile);
                // if (TargetImage == null) throw new Exception("Unknown error");
            } catch (Exception e) {
                Path currentRelativePath = Paths.get("");
                String s = currentRelativePath.toAbsolutePath().toString();
                state.output.fatal("Error loading target image: " + e.toString() + " in " + s);
            }

            if( TargetImage.getHeight() != size || TargetImage.getWidth() != size )
                state.output.fatal("Error loading target image: problem requires dimensions of " + size + "x" + size);
            state.output.message("Successfully loaded image");
        } //if
    } //setup
} //ImageLoad
