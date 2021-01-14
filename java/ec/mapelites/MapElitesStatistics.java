package ec.mapelites;

import ec.*;
import ec.gp.*;
import java.io.*;
import ec.util.*;
import ec.simple.*;
import java.util.*;
import java.util.Map;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Paths;


/*
 * MapElitesStatistics.java
 *
 * Created: 13 May, 2020
 * By: Derek Hon
 */

public class MapElitesStatistics extends Statistics {
    public static final String P_DO_DEPTH = "do-depth";
    public static final String P_STATISTICS_MODULUS = "modulus";
    public static final String P_STATISTICS_FILE = "file";
    public static final String P_COMPRESS = "gzip";
    public static final String P_DO_GRAPH = "doGraph";
    public static final String P_DO_FINAL = "do-final";
    public static final String P_DO_DESCRIPTION = "do-description";
    public static final String P_SAVE_ALL = "saveAll";
    public static final String P_SAVE_FITTEST = "save-fittest";
    public static final String P_FITTEST = "fittest-amount";
    public static final String P_SAVE_PATH = "save-path";
    public static final String P_MAP_NAME = "map-name";

    public String savePath;
    public String mapName;

    public boolean doDepth;
    public boolean doGraph;
    public boolean doFinal;
    public boolean doDescription;
    public boolean saveAll;
    public boolean saveFit;

    public int fitAmount;
    public int modulus;
    public int statisticslog = 0;  // stdout by default

    long[] totalDepthSoFarTree;
    long[] totalSizeSoFarTree;
    long totalIndsSoFar;

    public Elite bestSoFar;
    public long totalAssessed;
    public double totalFitness;

    public MapElitesGraph graph;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state,base);

        File statisticsFile = state.parameters.getFile(
                base.push(P_STATISTICS_FILE),null);

        modulus = state.parameters.getIntWithDefault(base.push(P_STATISTICS_MODULUS), null, 1);

        if (statisticsFile != null) {
            try {
                statisticslog = state.output.addLog(statisticsFile,
                        !state.parameters.getBoolean(base.push(P_COMPRESS),null,false),
                        state.parameters.getBoolean(base.push(P_COMPRESS),null,false));
            } //try
            catch (IOException i) {
                state.output.fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + i);
            } //catch
        } //if
        else state.output.warning("No statistics file specified, printing to stdout at end.", base.push(P_STATISTICS_FILE));

        state.output.message(base.push(P_DO_GRAPH).toString());
        saveAll = state.parameters.getBoolean(base.push(P_SAVE_ALL), null, true);
        saveFit = state.parameters.getBoolean(base.push(P_SAVE_FITTEST), null, false);
        doDepth = state.parameters.getBoolean(base.push(P_DO_DEPTH),null,false);
        doGraph = state.parameters.getBoolean(base.push(P_DO_GRAPH), null, false);
        doFinal = state.parameters.getBoolean(base.push(P_DO_FINAL),null,true);
        doDescription = state.parameters.getBoolean(base.push(P_DO_DESCRIPTION),null,true);
        fitAmount = state.parameters.getInt(base.push(P_FITTEST), null, 1);
        savePath = state.parameters.getString(base.push(P_SAVE_PATH), null);
        mapName = state.parameters.getString(base.push(P_MAP_NAME), null);

        if (doGraph)
            graph = new MapElitesGraph();
    } //setup

    public Elite getBestSoFar() { return bestSoFar; }

    public void postInitializationStatistics(final MapElitesEvolutionState state) {
        super.postInitializationStatistics((EvolutionState)state);

        bestSoFar = null;

        totalIndsSoFar = 0L;


        if ( !(state.map.species instanceof MapElitesGPSpecies ))
            state.output.fatal("Map is not of the species form MapElitesGPSpecies." +
                    "  Cannot do timing statistics with KozaShortStatistics.");

        GPIndividual i = (GPIndividual)(state.map.map.values().toArray()[0]);
        totalDepthSoFarTree = new long[i.trees.length];

        if (doGraph)
            graph.initialGraph(state.map.map, state.map.behaviours, state);
    }

    protected void prepareStatistics(MapElitesEvolutionState state) {
        GPIndividual i = (GPIndividual)(state.map.map.values().toArray()[0]);
        totalDepthSoFarTree = new long[i.trees.length];
    } //prepareStatistics

    protected void gatherTreeStats(MapElitesEvolutionState state, String key) {
        GPIndividual ind = (GPIndividual)(state.map.getElite(key));
        for (int i = 0 ; i < ind.trees.length ; i ++) {
            totalDepthSoFarTree[i] += ind.trees[i].child.depth();
            totalSizeSoFarTree[i] += ind.trees[i].child.numNodes(GPNode.NODESEARCH_ALL);
        } //for
    } //gatherTreeStats

    public void postEvaluationStatistics(final MapElitesEvolutionState state) {
        super.postEvaluationStatistics((EvolutionState)state);

        boolean output = (state.evaluations % modulus == 0);

        totalAssessed = 0L;
        bestSoFar = null;
        totalFitness = 0.0;
        double meanFitness = 0.0;

        for (Map.Entry<String, Elite> entry : state.map.map.entrySet()) {
            if (state.map.getElite(entry.getKey()).evaluated) {

                if (bestSoFar == null ||
                        state.map.getElite(entry.getKey()).fitness.betterThan(bestSoFar.fitness)
                ) {
                    bestSoFar = (Elite)state.map.getElite(entry.getKey()).clone();
                } //if

                totalFitness += state.map.getElite(entry.getKey()).fitness.fitness();
//                gatherTreeStats(state, entry.getKey());
            } //if
        } //for

        //mean fitness
        meanFitness = totalFitness/state.map.getSize();

        if (output) {
            state.output.println("\nEvaluation: " + state.evaluations, statisticslog);
            state.output.print("Mean Fitness: " + meanFitness, statisticslog);
            state.output.print("\nBest Fitness: " + bestSoFar.fitness.fitness() + "", statisticslog);
            state.output.message("Mean Fitness so far: " + meanFitness);
            //mean depth
//            if (doDepth) {
//                state.output.print("[ ", statisticslog);
//                for (int i = 0 ; i < totalDepthSoFarTree.length ; i ++)
//                    state.output.println("" + (double)totalDepthSoFarTree[i]/state.map.getSize() + " ", statisticslog);
//                state.output.print("] ", statisticslog);
//            } //if
        } //if

        Elite bestInd = null;
        for (Map.Entry<String, Elite> entry : state.map.map.entrySet()) {
            if (state.map.getElite(entry.getKey()) != null &&
                    (bestInd == null ||
                            state.map.getElite(entry.getKey()).fitness.betterThan(bestInd.fitness))) {
                bestInd = state.map.getElite(entry.getKey());
            } //if
        } //for

        if (bestSoFar == null || bestInd.fitness.betterThan(bestSoFar.fitness))
            bestSoFar = (Elite)bestInd.clone();

        if (doGraph) graph.drawFrame(state.map.map, state.map.behaviours, state);
    } //postEvaluationStatistics

    public void finalStatistics(final MapElitesEvolutionState state, final int result) {
        super.finalStatistics((EvolutionState)state,result);

        // for now we just print the best fitness

        state.output.message("Performing final statistics");

        if (doFinal) state.output.println("\nBest Individual of Run:",statisticslog);
        if (doFinal) bestSoFar.printIndividualForHumans(state,statisticslog);
        if (doFinal) state.output.message("Best fitness of run: " + bestSoFar.fitness.fitnessToStringForHumans());

        // finally describe the winner if there is a description
        if (doFinal && doDescription)
            if (state.evaluator.p_problem instanceof MapElitesProblemForm)
                ((MapElitesProblemForm)(state.evaluator.p_problem.clone())).describe(state, bestSoFar, 0, statisticslog);
        if (graph == null) {
            graph = new MapElitesGraph();
            graph.initialGraph(state.map.map, state.map.behaviours, state);
        }
        else
            graph.drawFrame(state.map.map, state.map.behaviours, state);

        // state.output.message("save: " + saveAll);
        if (saveAll) {
            state.output.message("Saving images...");
            Iterator mapIter = state.map.map.entrySet().iterator();
            Path cwd = Paths.get("").toAbsolutePath();
            state.output.message("exists: " + new File("src/main/resources/ec/app/mapelites").exists());

            while (mapIter.hasNext()) {
                Map.Entry element = (Map.Entry)mapIter.next();
                String key = element.getKey().toString();
                state.output.println("\nIndividual " + key + ":", statisticslog);
                ((Elite) element.getValue()).printIndividualForHumans(state, statisticslog);
//                state.output.message(cwd.toString() + savePath + key.replace('.', '_') + ".png");
                try {
                    BufferedImage bImage = ((Elite) element.getValue()).render;
                    File image = new File(savePath + key.replace('.', '_') + ".png");
                    ImageIO.write(bImage, "png", image);
                } catch (IOException e) {
                    state.output.fatal("Error writing image to file");
                } //catch
            } //while
            try {
                BufferedImage bImage = graph.saveAsImage();
                File image = new File(savePath + "/map/" + mapName + ".png");
                ImageIO.write(bImage, "png", image);
            } catch (IOException e) {
                state.output.fatal("Error writing image to file");
            } //catch
            state.output.message("Saved all images.");
        } //if
    } //finalStatistics
}