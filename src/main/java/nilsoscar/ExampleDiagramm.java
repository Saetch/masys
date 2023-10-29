package nilsoscar;

import nilsoscar.environment.EnvData;
import nilsoscar.environment.Environment;
import nilsoscar.environment.Situation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class ExampleDiagramm {

    public static void main(String[] args) {
        generateImageAsTest();
        generateReferenceHistogram();
    }

    private static void generateImageAsTest() {
        Environment testEnvironment = new Environment(3.6, 2, 10, 1.5);
        double[] testData = generateRealWorldData(testEnvironment);
        JFreeChart histogrammChart = generateChart(testData);
        saveChartAsPNG("testChart.png", 800, 600, histogrammChart);
    }

    private static void generateReferenceHistogram() {
        Environment environment = new Environment(3.6, 5.0, 6.3, 4.0);
        Map<Situation, Double[]> referenceData = createRawWorld(environment);
        JFreeChart coloredExample = generateChart(referenceData);
        saveChartAsPNG("referenceChart.png", 800, 600, coloredExample);
    }

    private static double[] parseDoubleToPrimitiv(Double[] input) {
        double[] result = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            if (input[i] != null)
                result[i] = input[i];
        }

        return result;
    }

    private static JFreeChart generateChart(Map<Situation, Double[]> fancyRealWorld) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);

        double[] harmlessData = parseDoubleToPrimitiv(fancyRealWorld.get(Situation.HARMLESS));
        double[] dangerData = parseDoubleToPrimitiv(fancyRealWorld.get(Situation.DANGER));
        int numberOfBins = 50;

        dataset.addSeries("Harmless", harmlessData, numberOfBins);
        dataset.addSeries("Danger", dangerData, numberOfBins);

        JFreeChart chart = ChartFactory.createHistogram(
                "Wahrscheinlichkeitsverteilung",
                "Werte",
                "Häufigkeit",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(255, 0, 0, 128)); // Rot mit 50% Transparenz für Dataset 1
        plot.getRenderer().setSeriesPaint(1, new Color(0, 0, 255, 128)); // Blau mit 50% Transparenz für Dataset 2

        return chart;
    }

    private static JFreeChart generateChart(double[] dataFromEvn) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        double[] data = dataFromEvn;
        int numberOfBins = 50;
        dataset.addSeries("Data", data, numberOfBins);

        JFreeChart chart = ChartFactory.createHistogram(
                "Wahrscheinlichkeitsverteilung",
                "Werte",
                "Häufigkeit",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        return chart;
    }

    private static double[] generateRealWorldData(Environment environment) {
        int sampleSize = 100_000;
        double[] result = new double[sampleSize];

        for (int i = 0; i < sampleSize; i++) {
            result[i] = environment.nextValue().measurement();
        }

        return result;
    }

    private static Map<Situation, Double[]> createRawWorld(Environment environment) {
        Map<Situation, Double[]> resultMap = new TreeMap<>();
        int sampleSize = 100_000;
        Double[] harmless = new Double[sampleSize/2];
        Double[] danger = new Double[sampleSize/2];


        int harmlessCounter = 0;
        int dangerCounter = 0;
        for (int i = 0; i < sampleSize; i++) {
            EnvData envData = environment.nextValue();
            if (envData.reality() == Situation.HARMLESS && (harmlessCounter) < harmless.length) {
                harmless[harmlessCounter] = envData.measurement();
                harmlessCounter++;
            } else if (envData.reality() == Situation.DANGER && (dangerCounter) < danger.length) {
                danger[dangerCounter] = envData.measurement();
                dangerCounter++;
            }
        }

        resultMap.put(Situation.HARMLESS, harmless);
        resultMap.put(Situation.DANGER, danger);

        return resultMap;
    }

    private static void saveChartAsPNG(String filename, int width, int height, JFreeChart chart) {
        try {
            ChartUtils.saveChartAsPNG(new File(filename), chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

