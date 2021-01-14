package ec.app.testmap;


import java.awt.Color;
import java.awt.image.*;
import java.util.ArrayList;


/**
 * This class is used both to store color histogram information and compare
 * its own values to that of other color histograms.
 *
 * Adaptations by Michael Gircys, 2017-06-08
 * Precomputed and stored bin colour similarity, instead of thousands of times across generations.
 *
 * @author Steve Bergen
 */

public class DirectMatch {
    public int[]                    data    = null;
    public BufferedImage            img     = null;
    public double                   pixels  = 0.0;

    /**
     * Constructor which takes in an image as source and stores all pixel rgb values.
     * @param img           Source image
     */
    public DirectMatch ( BufferedImage img ) {
        this.img = img;
        this.pixels = img.getHeight() * img.getWidth();
        this.data = new int[img.getHeight() * img.getWidth()];

        for (int i = 0 ; i < img.getHeight() ; i ++) {
            for (int j = 0 ; j < img.getWidth() ; j ++)
                this.data[i * img.getWidth() + j] = img.getRGB(j, i);
        } //for
    } //DirectMatch

    public DirectMatch ( int[] pixelValues ) {
        this.data = pixelValues;
    }

    private double blueDistance( DirectMatch match, int i ) {
        Color colourOne = new Color(match.data[i]),
                colourTwo = new Color(this.data[i]);

        int blueDistance = colourTwo.getBlue() - colourOne.getBlue();

        return Math.sqrt(Math.pow(blueDistance, 2));
    }

    /**
     *
     * @param match  Second Image
     * @param i      Index for the pixel
     * @return
     */
    private double colourDistance( DirectMatch match, int i ) {
        Color colourOne = new Color(match.data[i]),
              colourTwo = new Color(this.data[i]);

        int redDistance = colourTwo.getRed() - colourOne.getRed();
        int blueDistance = colourTwo.getBlue() - colourOne.getBlue();
        int greenDistance = colourTwo.getGreen() - colourOne.getGreen();

        return Math.sqrt(
                Math.pow(redDistance, 2) + Math.pow(blueDistance, 2)
                        + Math.pow(greenDistance, 2));
    } //colourDistance

    /**
     * Returns difference between two images directly compared to one another
     * @param match  Second image
     * @return       Colour distance
     */
    public double[] colourDirectMatch ( DirectMatch match ) {
        double total = 0.0,
               hits  = 0.0;

        for ( int i = 0 ; i < this.pixels ; i ++ ) {
            double val = colourDistance(match, i);
            total += val;
            if (val <= 3)
                hits ++;
        }
        return new double[] {total, hits};
    } //colourDirectMatch

    public double[] blueMatch ( DirectMatch match ) {
        double total = 0.0,
                hits  = 0.0;

        for ( int i = 0 ; i < this.pixels ; i ++ ) {
            double val = blueDistance(match, i);
            total += val;
            if (val == 0)
                hits ++;
        }
        return new double[] {total, hits};
    }


//Should probably reformat and use to test direct match but oh well
//    public static void main( String[] args )
//    {
//        int w = 128;
//        int h = 128;
//
//        BufferedImage i1 = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
//        BufferedImage i2 = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
//        for(int y = 0; y < h; y++)
//            for(int x = 0; x < w; x++)
//            {
//                i1.setRGB(x, y, 0x00000000);
//                i2.setRGB(x, y, 0xFFFFFFFF);
//            }
//
//        ColorHistogram h1 = new ColorHistogram(i1);
//        ColorHistogram h2 = new ColorHistogram(i2);
//        double d0 = h1.colorDS(h1);
//        double d1 = h1.colorDS(h2);
//
//        System.out.println("Min Hist Distance: " + String.valueOf(d0));
//        System.out.println("Max Hist Distance: " + String.valueOf(d1));
//    }

};
