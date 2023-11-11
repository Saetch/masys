package nilsoscar;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportDias {

    final static String singleTest = "single_test_change_env_at_400_draws_70k_tries.csv";
    final static String quorumTest = "quorum_test_continuous_values_env_change_at_100_with_100_agents.csv";
    final static String differentAgents = "quorum_test_number_of_agents_points_after_150_draws.csv";

    public static void main(String[] args) {
        createFirstChart();
        createSecondChart();
        createThirdChart();
    }

    private static void createThirdChart() {
        double[][] data = readFromVSC(differentAgents);
        JFreeChart chart = generateScatterPlot(data,
                "Gesammelte Punkte für jeweilige Gruppengröße",
                "Durchschnitt der gesammelten Punkte pro Zug als Gruppe mit wachsender Gruppengröße",
                "Agenten",
                "Gesammelte Punkte");
        saveChartAsPNG("differentAgents.png", 1200, 400, chart);
    }

    private static void createSecondChart() {
        double[][] data = readFromVSC(quorumTest);
        JFreeChart chart = generateScatterPlot(data,
                "Gesammelte Punkte für den jeweiligen Zug",
                "Durchschnitt der gesammelten Punkte pro Zug als Gruppe von 100 Agenten mit Quorumsregel",
                "Züge",
                "Gesammelte Punkte");
        saveChartAsPNG("quorumsRun.png", 1200, 400, chart);
    }

    private static void createFirstChart() {
        double[][] data = readFromVSC(singleTest);
        JFreeChart chart = generateScatterPlot(data,
                "Gesammelte Punkte für den jeweiligen Zug",
                "Verlauf der gesammelten Punkte im Durchschnitt eines einzelnen Agenten",
                "Züge",
                "Gesammelte Punkte");
        saveChartAsPNG("singleAgent.png", 1200, 400, chart);
    }

    private static double[][] readFromVSC(String nameOfFile) {
        List<DataRow> rawData = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(nameOfFile))) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                int firstValue = (int) Double.parseDouble(line[0]);
                double secondValue = Double.parseDouble(line[1]);
                rawData.add(new DataRow(firstValue, secondValue));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        double[][] result = new double[rawData.size()][2];

        for (int i = 0; i < rawData.size(); i++) {
            DataRow row = rawData.get(i);
            result[i][0] = row.draw();
            result[i][1] = row.value();
        }

        return result;
    }

    private static JFreeChart generateScatterPlot(double[][] input, String legendForDataPoints, String title, String xAxisLabel, String yAxisLabel) {
        XYSeries data = new XYSeries(legendForDataPoints);

        for (double[] doubles : input) {
            data.add(doubles[0], doubles[1]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(data);

        JFreeChart chart = ChartFactory.createScatterPlot(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        
        renderer.setSeriesShape(0, new Ellipse2D.Double(-1, -1, 3, 3));
        renderer.setSeriesShapesVisible(0, true);

        plot.setRenderer(renderer);

        return chart;
    }

    private static void saveChartAsPNG(String filename, int width, int height, JFreeChart chart) {
        try {
            ChartUtils.saveChartAsPNG(new File(filename), chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

record DataRow(int draw, double value) {}