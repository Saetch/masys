package nilsoscar.environment;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class Environment {

    private final NormalDistribution danger;
    private final NormalDistribution harmless;
    private final Random random;

    public Environment(double harmlessMean, double harmlessStdDev, double dangerMean, double dangerStdDev) {
        this.harmless = new NormalDistribution(harmlessMean, harmlessStdDev);
        this.danger = new NormalDistribution(dangerMean,  dangerStdDev);
        this.random = new Random();
    }

    public EnvData nextValue() {
        // flip a coin for situation
        int randomInt = random.nextInt(2);
        EnvData result;
        // get sample from corresponding distribution
        if (randomInt == 0) {
            result = new EnvData(harmless.sample(), Situation.HARMLESS);
        } else {
            result = new EnvData(danger.sample(), Situation.DANGER);
        }
        return result;
    }

}
