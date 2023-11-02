package nilsoscar.agents;

import java.util.ArrayList;
import java.util.List;

import nilsoscar.environment.EnvData;
import nilsoscar.environment.Environment;
import nilsoscar.environment.Situation;

public class Quorum {
    public List<Agent> agents = new ArrayList<>();
    public double threshold = 0.51;
    public Environment environment;





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
        if(danger_counter > threshold * agents.size()){
            return Situation.DANGER;
        }
        return Situation.HARMLESS;


    }
}
