package nilsoscar.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import nilsoscar.environment.EnvData;
import nilsoscar.environment.Situation;

public class Agent {
    private List<Double>[] measurements;
    public int minimumRequiredMatches = 24;


    public Agent() {
        this.measurements = new ArrayList[2];
    }

    public int getHarmlessCount(){
        if (measurements[0] == null){
            return 0;
        }
        return measurements[0].size();
    }

    public int getDangerCount(){
        if (measurements[1] == null){
            return 0;
        }
        return measurements[1].size();
    }

    public Situation decideOn(EnvData envData) {

        for ( List<Double> meas : measurements){
            if(meas == null){
                //if there is no data to go by, just guess
                return Situation.values()[(int) (Math.random() * Situation.values().length)];
            }
        }


        
        Double harmless_mean = measurements[0].stream().mapToDouble(a -> a).average().getAsDouble();
        Double danger_mean = measurements[1].stream().mapToDouble(a -> a).average().getAsDouble();

        //Vorwissen laut Aufgabenstellung, mittelwert für gefährlich > ungefährlich, also wenn nicht der Fall, dann keine brauchbaren Daten vorhanden
        if(harmless_mean >= danger_mean ){
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }


        //if it is outside of the contested range, we should be able to guess the more likely solution
        if(envData.measurement() <= harmless_mean){
            return Situation.HARMLESS;
        }
        if(envData.measurement() >= danger_mean){
            return Situation.DANGER;
        }

        //use successive approximation on the own data for finding the right t? Idk, I'm just doing it
        Situation retVal = this.checkDistToClosestNeighbors(envData.measurement());



        return retVal;
    }


    private Situation checkDistToClosestNeighbors(Double point){

        int len = measurements[0].size() < measurements[1].size() ? measurements[0].size() : measurements[1].size();

        //in order to make this fair, we need to limit the stream to the length of the shorter list, otherwise the other one would likely get more or better hits. This should not matter for big enough lists
        Stream<Double> harmlessStream = measurements[0].stream().limit(len);
        Stream<Double> dangerStream = measurements[1].stream().limit(len);
        int numberOfNeighbors = this.minimumRequiredMatches > len ? len : this.minimumRequiredMatches;

        //find the closest neighbors 
        DoubleStream harmlessNeighbors = harmlessStream.mapToDouble(m -> Math.abs(m - point)).sorted().limit(numberOfNeighbors);
        DoubleStream dangerNeighbors = dangerStream.mapToDouble(m -> Math.abs(m - point)).sorted().limit(numberOfNeighbors);
    
        //get the average distance to the neighbors
        double harmlessAvg = harmlessNeighbors.average().getAsDouble();
        double dangerAvg = dangerNeighbors.average().getAsDouble();
        
        //if the distance to the harmless neighbors is smaller, it is more likely to be harmless
        if(harmlessAvg < dangerAvg){
            return Situation.HARMLESS;
        }
        return Situation.DANGER;
    
    }


    public int addMeasurement(EnvData envData) {
        int index = envData.reality().ordinal();
        if(measurements[index] == null){
            measurements[index] = new ArrayList<Double>();
        }
        measurements[index].add(envData.measurement());

        //check if any of these is null
        if(measurements[0] == null || measurements[1] == null){
            return 0;
        }
        int lower_len = measurements[0].size() < measurements[1].size() ? measurements[0].size() : measurements[1].size();

        //I chose an arbitrary rule to set the number of required matches, in this case y = 2x² for a more sophisticated approach, this should propably be tested for what works best
        //if( lower_len > 2*Math.pow(this.minimumRequiredMatches, 2)){
        //    this.minimumRequiredMatches++;
        //}

        return lower_len;
    }

  
    

}

record DoubleTuple(double harmless, double danger){};