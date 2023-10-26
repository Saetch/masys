package nilsoscar;

import nilsoscar.environment.Environment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.io.File;
import java.io.IOException;

public class ExampleDiagramm {

    public static void main(String[] args) {
//        Environment environment = new Environment(3.6, 5.0, 6.3, 4.0);
        Environment environment = new Environment(3.6, 2, 10, 1.5);
        double[] fancyData = generateRealWorldData(environment);

        JFreeChart example = generateChart(fancyData);
        saveChartAsPNG("realWorld.png", 800, 600, example);
    }

    private static JFreeChart generateChart(double[] dataFromEvn) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        double[] data = dataFromEvn;
        int numberOfBins = 300;
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
        int sampleSize = 10_000;
        double[] result = new double[sampleSize];

        for (int i = 0; i < sampleSize; i++) {
            result[i] = environment.nextValue().measurement();
        }

        return result;
    }

    private static void saveChartAsPNG(String filename, int width, int height, JFreeChart chart) {
        try {
            ChartUtils.saveChartAsPNG(new File(filename), chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

