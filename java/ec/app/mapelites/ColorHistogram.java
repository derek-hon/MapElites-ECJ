package ec.app.testmap;


import java.awt.Color;
import java.awt.image.*;


/**
 * This class is used both to store color histogram information and compare
 * its own values to that of other color histograms.
 * 
 * Adaptations by Michael Gircys, 2017-06-08
 * Precomputed and stored bin colour similarity, instead of thousands of times across generations.
 *
 * @author Steve Bergen
 */

public class ColorHistogram 
{

	private static final double     Max_Colour_Dist       = Math.sqrt( Math.pow(255,2) * 3);
    public static final double[][]  BIN_COLOUR_SIMILARITY = new double[512][512];
    
	public double[]                 data    = null;
	public BufferedImage            img     = null;
    

	/**
	 * Constructor which takes in an image as source for initial calculations.
	 * @param img           Source image
	 */
	public ColorHistogram ( BufferedImage img ) 
	{
		this(img,1);
	}	
	public ColorHistogram ( BufferedImage img, int threads )
	{   
        this.img  = img;
        this.data = new double[512];
        for ( int i = 0; i < 512; i++ )
            this.data[i] = 0;

        int start = 0;
        int inc = img.getHeight() / threads;
        
        // Create and fire off additional threads as needed
        HistThread fit[] = new HistThread[threads-1];
        if(threads > 1)
        {   
	        for (int i = 0; i < threads-1; i++) 
	        {
	            fit[i] = new HistThread(img, start, start + inc, data.clone());
	            start = start + inc;
	            fit[i].start();
	        }
        }

        // Do work alloted for first thread
        quantize(img, start, img.getHeight(), data);
        
        if(threads > 1)
        {
        	// Check for all finished threads, and quit when all are finished
	        while (true) 
	        {
	            boolean dead = true;
	            for (int i = 0; i < threads-1; i++)
	                if (fit[i].isAlive()) dead = false;
	            if (dead) break;
	        }
	        // Copy histogram data from threads to main instance
	        for ( int i = 0; i < 512; i++ )
	        	for (int j = 0; j < threads-1; j++)
	        		this.data[i] += fit[j].data[i];
        }
        
        // Normalize
        int pixelCount = img.getWidth() * img.getHeight();
        for ( int i = 0; i < 512; i++ )
        	this.data[i] /= (double)pixelCount;
	};
    
	static
	{
		for ( int i = 0; i < 512; i++ )
        	for ( int j = 0; j < 512; j++ )
        		BIN_COLOUR_SIMILARITY[i][j] = colorSimilarity(getColorI(i),getColorI(j));
	}
	
	
	private static class HistThread extends Thread 
	{
		
	    public double[]         data;
	    public int              ystart, yend;
	    private BufferedImage   img;

	    public HistThread ( BufferedImage img, int ystart, int yend, double[] data ) 
	    {
	        this.img        = img;
	        this.ystart     = ystart;
	        this.yend       = yend;
	        this.data       = data;
	    };

	    @Override
	    public void run ( ) 
	    {
	        ColorHistogram.quantize(img, ystart, yend, data);
	    };
	    
	};
	

    /**
     * Method called to quantize an image to 512 colors, and counts the
     * occurrences of each color in the image, storing them in an array.
     * @param img           Source image
     */
    public static void quantize ( BufferedImage img, int y0, int yend, double[] d ) 
    {   
    	for ( int x = 0; x < img.getWidth(); x++ )
    		for ( int y = y0; y < yend; y++ )
    			d[getIndex(new Color(img.getRGB(x, y)))]++;
    };
    

    /**
     * Returns an integer representing an index from 0 to 511, which is the
     * value of the color passed in.
     * @param c             Color to be quantized
     * @return              Quantized integer
     */
    private static int getIndex ( Color c ) 
    {   
        int index = 0;
        // TODO: Make more clear by switching to bitshifts.
        index   =  (c.getRed()   / 32) * 64;
        index   += (c.getGreen() / 32) * 8;
        index   += (c.getBlue()  / 32);
        return index;
    };
    

    /**
     * Calculates the color index distance between two histograms at a single
     * index i.
     * @param hist          Second histogram
     * @param i             Index
     * @return              Absolute distance between values.
     */
    private double colorDistanceI ( ColorHistogram hist, int i ) 
    {   
        return Math.abs(hist.data[i] - this.data[i]);
    };
    
    
    /**
     * Calculates the color similarity between two colors.
     * @param a             First color
     * @param b             Second color
     * @return              Color similarity value
     */
    private static double colorSimilarity ( Color a, Color b ) 
    {   
        double R    = Math.pow(a.getRed()   - b.getRed(),   2);
        double G    = Math.pow(a.getGreen() - b.getGreen(), 2);
        double B    = Math.pow(a.getBlue()  - b.getBlue(),  2);
        double t    = Math.sqrt(R + G + B);
        double res  = Math.pow(1.0 - (t / Max_Colour_Dist), 2); 
        return res;
    };
    private static double colorSimilarity( int ind1, int ind2 )
    {
    	return BIN_COLOUR_SIMILARITY[ind1][ind2];
    }
    

    /**
     * Converts an integer from 0..511 to a color (reverse-quantization).
     * @param i             Integer to convert
     * @return              Color value of integer
     */
    private static Color getColorI ( int i ) 
    { 
        int R = ((i / 64)    ) * 32;
        int G = ((i % 64) / 8) * 32;
        int B = ((i % 64) % 8) * 32;
        return new Color(R, G, B);
    };
    

    /**
     * Returns the color distance between the histograms summed with absolute
     * color distance between two histogram images. An expensive call.
     * @param hist          Second histogram
     * @return              Color distance
     */
    public double colorDS ( ColorHistogram hist ) 
    {   
        double total = 0.0;
        for ( int i = 0; i < 512; i++ )
        	for ( int j = 0; j < 512; j++ ) 
        		total += colorDistanceI(hist, i) 
                       * colorSimilarity(i,j) 
                       * colorDistanceI(hist, j);
        return total;
    };
    

    /** Returns the color index difference between two histograms. Compares
     * values stored in the arrays.
     * @param hist          Second histogram
     * @return              Color distance
     */
    public double colorHistDistance ( ColorHistogram hist ) 
    {   
        double total = 0.0;
        for ( int i = 0; i < 512; i++ )
            total += colorDistanceI(hist, i);
        return total;
    };
    
    
    
    
    
    
    public static void main( String[] args )
    {
    	int w = 128;
    	int h = 128;
    	
    	BufferedImage i1 = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
    	BufferedImage i2 = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
    	for(int y = 0; y < h; y++)
    	for(int x = 0; x < w; x++)
    	{
    		i1.setRGB(x, y, 0x00000000);
    		i2.setRGB(x, y, 0xFFFFFFFF);
    	}
    	
    	ColorHistogram h1 = new ColorHistogram(i1);
    	ColorHistogram h2 = new ColorHistogram(i2);
    	double d0 = h1.colorDS(h1);
    	double d1 = h1.colorDS(h2);
    	
    	System.out.println("Min Hist Distance: " + String.valueOf(d0));
    	System.out.println("Max Hist Distance: " + String.valueOf(d1));
    }
    
};
