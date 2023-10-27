package nilsoscar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

import nilsoscar.HelperClasses.NormalDistributionHelper;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        NormalDistributionHelper harmless = new NormalDistributionHelper(3.5, 5.0);
        NormalDistributionHelper dangerous = new NormalDistributionHelper(6.3, 4.0);


        // Create a dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<Double> list_h = new ArrayList<Double>();
        List<Double> list_d = new ArrayList<Double>();


        
        for (int i = 0; i < 120000; i++){
            DistrResult result = genValue(harmless, dangerous);
            if (result.harmless){
                list_h.add(result.value);
            } else {
                list_d.add(result.value);
            }
        }

        Double[] array_h = list_h.toArray(new Double[list_h.size()]);
        Double[] array_d = list_d.toArray(new Double[list_d.size()]);



        // Create a chart
        JFreeChart chart = ChartFactory.createBarChart("Distribution Chart", "X-Axis", "Y-Axis", dataset, PlotOrientation.VERTICAL, true, true, false);
    }



    public static DistrResult genValue(NormalDistributionHelper harmless, NormalDistributionHelper dangerous){
        DistrResult result = new DistrResult();
        boolean isHarmless = Math.random() < 0.5;
        result.harmless = isHarmless;
        if (isHarmless){
            result.value = harmless.getValue();
        } else {
            result.value = dangerous.getValue();
        }
        return result;
    }


}

class DistrResult{
    boolean harmless;
    double value;
}
