package nilsoscar.HelperClasses;


import org.apache.commons.math3.distribution.NormalDistribution;




public class NormalDistributionHelper {
    NormalDistribution normalDistribution;


    public NormalDistributionHelper(double mean, double stdDev) {
        normalDistribution = new NormalDistribution(mean, stdDev);
    }

    public double getMean() {
        return normalDistribution.getMean();
    }

    public double getStdDev() {
        return normalDistribution.getStandardDeviation();
    }

    public NormalDistributionHelper setMean(double mean) {
        double stdDev = normalDistribution.getStandardDeviation();
        this.normalDistribution = new NormalDistribution(mean, stdDev);
        return this;
    }

    public NormalDistributionHelper setStdDev(double stdDev) {
        double mean = normalDistribution.getMean();
        this.normalDistribution = new NormalDistribution(mean, stdDev);
        return this;
    }

    public double getValue(){
        return normalDistribution.sample();
    }

}
