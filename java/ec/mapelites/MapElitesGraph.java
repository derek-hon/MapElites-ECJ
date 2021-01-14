package ec.mapelites;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import ec.util.*;

public class MapElitesGraph {

    GridLayout graph;
    JPanel[][] panels;
    JFrame frame;
    int totalY, totalX;

    public void initialGraph(HashMap<String, Elite> map, Behaviours[] behaviours, MapElitesEvolutionState state) {
        HashMap keyMap = transposeKeys(map, behaviours, state);
        frame = new JFrame("Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1000, 1000);
        graph = new GridLayout(behaviours[0].totalLimits() + 2, behaviours[1].totalLimits() + 2, 0, 0);

        totalY = behaviours[0].totalLimits() + 2;
        totalX = behaviours[1].totalLimits() + 2;

        panels = new JPanel[totalY][totalX];

        for (int i = 0; i < panels.length; i++) {
            for (int j = 0; j < panels[0].length; j++)
                panels[i][j] = new JPanel();
        } // for

        drawFrame(map, behaviours, state);

        for (int i = 0; i < panels.length; i++) {
            for (int j = 0; j < panels[0].length; j++)
                frame.getContentPane().add(panels[i][j]);
        } // for

        JLabel axisTitle = new JLabel(), axisTitle2 = new JLabel();
        axisTitle.setText(behaviours[0].getName());
        axisTitle2.setText(behaviours[1].getName());

        panels[totalY / 2][0].add(axisTitle);
        panels[totalY - 1][totalX / 2].add(axisTitle2);

        for (int i = behaviours[0].totalLimits() - 1; i > 0; i--) {
            String limit = behaviours[0].getLimit(i) + "";
            panels[behaviours[0].totalLimits() - 1 - i][1].add(new JLabel(limit));
        } // for

        for (int i = 2; i < totalX; i++) {
            String limit = behaviours[1].getLimit(i - 2) + "";
            panels[totalY - 2][i].add(new JLabel(limit));
        } // for

        frame.setLayout(new GridLayout(totalY, totalX));

        frame.setVisible(true);
    } // Graph

    public HashMap<String, int[]> transposeKeys(HashMap<String, Elite> map, Behaviours[] behaviours,
            MapElitesEvolutionState state) {
        HashMap<String, int[]> mapToReturn = new HashMap<String, int[]>();
        Set<String> keySet = map.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);

        for (int i = behaviours[0].totalLimits() - 1; i >= 0; i--) {
            for (int j = 0; j < behaviours[1].totalLimits(); j++) {
                for (String s : keys) {
                    if (s.equals(behaviours[0].getName() + behaviours[0].getLimit(i) + "" + behaviours[1].getName()
                            + behaviours[1].getLimit(j)))
                        mapToReturn.put(
                                behaviours[0].getName() + behaviours[0].getLimit(i) + "" + behaviours[1].getName()
                                        + behaviours[1].getLimit(j),
                                new int[] { behaviours[0].totalLimits() - 1 - i, j });
                } // for
            } // for
        } // for
        return mapToReturn;
    } // transposeKeys

    public void drawFrame(HashMap<String, Elite> map, Behaviours[] behaviours, MapElitesEvolutionState state) {
        HashMap<String, int[]> keyMap = transposeKeys(map, behaviours, state);

        totalY = behaviours[0].totalLimits() + 2;
        totalX = behaviours[1].totalLimits() + 2;

        Set<String> keySet = keyMap.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);

        BufferedImage img;

        for (String s : keys) {
            JPanel images = new JPanel();
            panels[keyMap.get(s)[0]][keyMap.get(s)[1] + 2].removeAll();

            img = generateImage(map.get(s).pixelValues, state);

            if (img == null) {
                state.output.fatal("null image");
            }
            panels[keyMap.get(s)[0]][keyMap.get(s)[1] + 2].add(new JLabel(new ImageIcon(img)));
            panels[keyMap.get(s)[0]][keyMap.get(s)[1] + 2].revalidate();
            panels[keyMap.get(s)[0]][keyMap.get(s)[1] + 2].repaint();
        } // for

        SwingUtilities.updateComponentTreeUI(frame);
    } // drawFrame

    public BufferedImage generateImage(int[] colours,  MapElitesEvolutionState state) {
        Parameter p = new Parameter("map");
        int imageSize = state.parameters.getInt(p.push("imagesize"), p.push("imagesize"), 0);
        BufferedImage img = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        for (int y = 0 ; y < imageSize ; y ++) {
            for (int x = 0 ; x < imageSize ; x ++) {
                img.setRGB(x, y, colours[y * imageSize + x]);
            }
        }
        if (img == null)
            state.output.fatal("Setting Image RGB in MapElitesGraph failed");
        return img;
    }

    // https://stackoverflow.com/questions/4725320/how-to-save-window-contents-as-an-image
    protected BufferedImage saveAsImage() {
        BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        frame.paint(graphics2D);

        return image;
    } // saveAsImage
} // Graph