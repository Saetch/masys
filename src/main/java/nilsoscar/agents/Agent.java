package nilsoscar.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import nilsoscar.environment.EnvData;
import nilsoscar.environment.Situation;

public class Agent {
    private Double[] measurements_harmless;
    private Double[] measurements_danger;
    public int minimumRequiredMatches = 5;
    public int points = 0;

    private int harmless_index = 0;
    private int danger_index = 0;

    private boolean any_harmless = false;
    private boolean any_danger = false;
    private boolean full_circle_harmless = false;
    private boolean full_circle_danger = false;
    public Agent() {
        this.measurements_harmless = new Double[10];
        this.measurements_danger = new Double[10];
    }



    public Situation decideOn(EnvData envData) {

        //not enough data do to anything
        if(!any_harmless){
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }
        if(!any_danger){
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }
        int len_harmless = full_circle_harmless ? measurements_harmless.length : harmless_index;
        int len_danger = full_circle_danger ? measurements_danger.length : danger_index;

        Double harmless_mean = Stream.of(measurements_harmless).limit(len_harmless).mapToDouble(a -> a).average().getAsDouble();
        Double danger_mean = Stream.of(measurements_danger).limit(len_danger).mapToDouble(a -> a).average().getAsDouble();

        //Vorwissen laut Aufgabenstellung, mittelwert für gefährlich > ungefährlich, also wenn nicht der Fall, dann keine brauchbaren Daten vorhanden
        if(harmless_mean >= danger_mean ){
            return Situation.values()[(int) (Math.random() * Situation.values().length)];
        }

        if(envData.measurement() <= harmless_mean){
            return Situation.HARMLESS;
        }
        if(envData.measurement() >= danger_mean){
            return Situation.DANGER;
        }

        //use successive approximation on the own data for finding the right t? Idk, I'm just doing it
        Situation retVal = this.checkDistToClosestNeighbors(envData.measurement(), harmless_mean, danger_mean);



        return retVal;
    }


    public void truePositive(){
        this.points++;
    }

    public void falsePositive(){
        this.points--;
    }

    public void resetPoints(){
        this.points = 0;
    }

    private Situation checkDistToClosestNeighbors(Double point, Double harmless_mean, Double danger_mean){
        int len_harmless = full_circle_harmless ? measurements_harmless.length : harmless_index;
        int len_danger = full_circle_danger ? measurements_danger.length : danger_index;
        int len = len_harmless < len_danger ? len_harmless : len_danger;

        //in order to make this fair, we need to limit the stream to the length of the shorter list, otherwise the other one would likely get more or better hits. This should not matter for big enough lists
        Stream<Double> harmlessStream = Stream.of(measurements_harmless).limit(len);
        Stream<Double> dangerStream = Stream.of(measurements_danger).limit(len);
        int numberOfNeighbors = this.minimumRequiredMatches > len ? len : this.minimumRequiredMatches;
        
        //find the closest neighbors 
        DoubleStream harmlessNeighbors = harmlessStream.limit(len).mapToDouble(m -> Math.abs(m - point)).sorted().limit(numberOfNeighbors);
        DoubleStream dangerNeighbors = dangerStream.limit(len).mapToDouble(m -> Math.abs(m - point)).sorted().limit(numberOfNeighbors);
    
        //get the average distance to the neighbors
        Double harmlessAvg = harmlessNeighbors.average().getAsDouble();
        Double dangerAvg = dangerNeighbors.average().getAsDouble();



        //attempt at evening out the by using an approximate average spread
        if(harmless_index > 2 && danger_index > 2){
            Stream<Double> harmless = Stream.of(measurements_harmless).limit(len);
            Stream<Double> danger = Stream.of(measurements_danger).limit(len);
            //need to reverse it, but Java Streams do not support reversing ...
            List<Double> harmless_spread = harmless.mapToDouble(m -> Math.abs(m - harmless_mean)).sorted().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            Collections.reverse(harmless_spread);
            long h_limit = (measurements_harmless.length/10)+2;
            long d_limit = (measurements_danger.length/10)+2;
            harmless = harmless_spread.stream().limit(h_limit);
            List<Double> danger_spread = danger.mapToDouble(m -> Math.abs(m - danger_mean)).sorted().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            Collections.reverse(danger_spread);
            danger = danger_spread.stream().limit(d_limit);
            long harmless_len = harmless.count();
            long danger_len = danger.count();
            danger = danger_spread.stream().limit(h_limit);
            harmless = harmless_spread.stream().limit(d_limit);
            Double avg_spread_harmless = harmless.skip(danger_len-2).mapToDouble(m->m).average().getAsDouble();
            Double avg_spread_danger = danger.skip(harmless_len-2).mapToDouble(m->m).average().getAsDouble();


            harmlessAvg /= avg_spread_harmless;
            dangerAvg /= avg_spread_danger;
        }


        //if the distance to the harmless neighbors is smaller, it is more likely to be harmless
        if(harmlessAvg < dangerAvg){
            return Situation.HARMLESS;
        }
        return Situation.DANGER;
    
    }


    public void addMeasurement(EnvData envData) {
        if ( envData.situation() == Situation.HARMLESS){
            this.any_harmless = true;
            this.measurements_harmless[this.harmless_index] = envData.measurement();
            this.harmless_index++;
            if(this.harmless_index >= this.measurements_harmless.length){
                this.harmless_index = 0;
                this.full_circle_harmless = true;
            }
        }else{
            this.any_danger = true;
            this.measurements_danger[this.danger_index] = envData.measurement();
            this.danger_index++;
            if(this.danger_index >= this.measurements_danger.length){
                this.danger_index = 0;
                this.full_circle_danger = true;
                
            }

        }

    }

  
    

}

record DoubleTuple(double harmless, double danger){};