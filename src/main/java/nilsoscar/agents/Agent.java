package nilsoscar.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import nilsoscar.environment.EnvData;
import nilsoscar.environment.Situation;

public class Agent {
    private List<Double>[] measurements;
    public Double neighborhoodSize = 0.1;
    public int minimumRequiredMatches = 3;


    public Agent() {
        this.measurements = new ArrayList[2];
    }

    public Situation decideOn(EnvData envData) {
        if(measurements[0] == null){
            measurements[0] = new ArrayList<Double>();
            //return a random decision, as we don't have enough data yet
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }
        if(measurements[1] == null){
            measurements[1] = new ArrayList<Double>();
            //return a random decision, as we don't have enough data yet
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }

        //this is used to just broadly set up the values that need to be checked for t 
        Double harmless_mean = measurements[0].stream().mapToDouble(a -> a).average().getAsDouble();
        Double danger_mean = measurements[1].stream().mapToDouble(a -> a).average().getAsDouble();
        

        //Vorwissen laut Aufgabenstellung, mittelwert für gefährlich > ungefährlich, also wenn nicht der Fall, dann keine brauchbaren Daten vorhanden
        if(harmless_mean <= danger_mean ){
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }


        //if it is outside of the contested range, we should be able to guess the more likely solution
        if(envData.measurement() < harmless_mean){
            return Situation.HARMLESS;
        }
        if(envData.measurement() > danger_mean){
            return Situation.DANGER;
        }

        //use successive approximation on the own data for finding the right t? Idk, I'm just doing it
        Situation retVal = this.successiveApproximation(envData, harmless_mean, danger_mean);


        return retVal;
    }


    private Situation successiveApproximation(EnvData envData, Double harmless_mean, Double danger_mean){
        Double lowerLimit = harmless_mean;
        Double upperLimit = danger_mean;
        Double check_range = this.neighborhoodSize;
        do{
            Double upper_hits = this.checkForSamplesInRange(upperLimit, check_range, Situation.DANGER);
            Double lower_hits = this.checkForSamplesInRange(lowerLimit, check_range, Situation.HARMLESS);
        }
        while(upperLimit - lowerLimit > neighborhoodSize);

    }

    //TODO! This needs rewriting and a more sophisticated approach
    private Double checkForSamplesInRange(Double point, Double range, Situation s){
        Double retVal = 0.0;
        Stream<Double> str;
        Double range_f = Math.abs(range);
        if(s == Situation.HARMLESS){
            str = measurements[0].stream();
        }
        else{
            str = measurements[1].stream();
        }
        //check for every value if it is in between the point and point + range
        DoubleStream stream = str.mapToDouble( a -> a).filter( a ->  a >= point-range_f && a <= point+range_f );
        retVal += stream.count();

        if (retVal >= this.minimumRequiredMatches){
            return retVal;
        }

        //if we dont have enough hits, we check the closest neighbors and see how far they are.
        int forcedMaximum = Math.min(this.measurements[0].size(), this.measurements[1].size());  //the smallest set is the maximum we can check
        int req_hits = Math.min(this.minimumRequiredMatches, forcedMaximum); //try to get minimum required matches, but if we have less data, we need to lower the requirement
        
        //check the closest neighbors
        DoubleStream stream2 = str.mapToDouble( a -> Math.abs(a - point)).sorted().limit(req_hits);
        retVal += stream2.max().getAsDouble();
        if (retVal >= this.minimumRequiredMatches){
            return retVal;
        }
        
    }

}
