package nilsoscar.agents;

import java.util.ArrayList;
import java.util.List;

import nilsoscar.environment.EnvData;
import nilsoscar.environment.Environment;
import nilsoscar.environment.Situation;

public class Quorum {
    public List<Agent> agents = new ArrayList<>();
    public double threshold = 0.50;
    public Environment environment;
    private double[] harmlessquorums = new double[10];
    private double[] dangerquorums= new double[10];
    private int harmless_index = 0;
    private int danger_index = 0;
    private boolean full_circle_harmless = false;
    private boolean full_circle_danger = false;



    public Situation quorum(EnvData envData){
        int danger_counter = 0;
        for (Agent agent : agents){
            EnvData agentData = environment.drawValue(envData.situation());
            Situation guess = agent.decideOn(agentData);
            if(guess == Situation.DANGER){
                danger_counter++;
            }



            /////////////////////// JUST GIVE POINTS AND ADD DATA
            if(guess == Situation.DANGER && envData.situation() == Situation.DANGER){
                agent.truePositive();
            }
            if(guess == Situation.DANGER && envData.situation() == Situation.HARMLESS){
                agent.falsePositive();
            }
            agent.addMeasurement(agentData);
            ////////////////////////
        }

        Situation retVal;
        if(danger_counter > threshold * agents.size()){
            retVal=  Situation.DANGER;
        }else{
            retVal= Situation.HARMLESS;
        }
        double posVal = danger_counter/(double)agents.size();
        if(envData.situation() == Situation.DANGER){
            dangerquorums[danger_index++] = posVal;
            if (danger_index >= dangerquorums.length){
              danger_index = 0;  
              full_circle_danger = true;
            } 
        }else{
            harmlessquorums[harmless_index++] = posVal ;
            if (harmless_index >= harmlessquorums.length) {
                harmless_index = 0;
                full_circle_harmless = true;
            }
        }


        int min_samples = Math.min(full_circle_danger ? dangerquorums.length : danger_index, full_circle_harmless ? harmlessquorums.length : harmless_index);
        int min_hits = 0;
        double max_dist = 0.0;
        if (min_samples != 0){
            for (double d = 0.0; d < 1.0; d = d + 0.01){
                int hits = getHits(d, min_samples);
                if (hits >= min_hits){
                    min_hits = hits;
                    double dist = getDist(d, min_samples);
                    if (dist > max_dist){
                        max_dist = dist;
                        this.threshold = d;
                    }
                }
            }
        }


        return retVal;
    }

    private int getHits(double thresh, int index){
        int hits = 0;
        for (int i = 0; i < index; i++){
            if (dangerquorums[i] > thresh){
                hits++;
            }
            if (harmlessquorums[i] < thresh){
                hits++;
            }
        }
        return hits;
    }

    private double getDist(double thresh, int index){
        double dist = 0.0;
        double h_dist = 0.0;
        double d_dist = 0.0;
        for (int i = 0; i < index; i++){
            if (dangerquorums[i] > thresh){
                double dist_d = Math.abs(dangerquorums[i] - thresh);
                if (dist_d < d_dist){
                    d_dist = dist_d;
                }
            }
            if (harmlessquorums[i] < thresh){
                double dist_h = Math.abs(harmlessquorums[i] - thresh);
                if (dist_h < h_dist){
                    h_dist = dist_h;
                }
            }
        }
        dist = h_dist + d_dist;
        return dist;
    }


    
}
