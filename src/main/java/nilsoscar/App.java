package nilsoscar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.function.Max;

import nilsoscar.agents.Agent;
import nilsoscar.agents.Quorum;
import nilsoscar.environment.EnvData;
import nilsoscar.environment.Environment;
import nilsoscar.environment.Situation;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {   

        quorumTest();
        // singleTest();
    }


    static void quorumTest(){
        final int MAX_DRAWS_PER_WORLD = 100;
        final int NUMBER_OF_AGENTS_BOUND = 200;
        final int NUMBER_OF_TRIES = 1_000;
        for (int agent_count = 1; agent_count <= NUMBER_OF_AGENTS_BOUND; agent_count++){
            List<Double>[] results = new ArrayList[MAX_DRAWS_PER_WORLD];
            List<Double>[] points = new ArrayList[MAX_DRAWS_PER_WORLD];
            for (int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                results[i] = new ArrayList<Double>();
                points[i] = new ArrayList<Double>();
            }

            for(int i = 0; i < NUMBER_OF_TRIES; i++){
                

                World world = new World();
                Environment environment = new Environment(3.5, 5.0, 6.3, 4.0);
                world.environment = environment;
                Quorum quorum = new Quorum();
                for (int j = 0; j < agent_count; j++){
                    quorum.agents.add(new Agent());
                }
                quorum.environment = environment;
                world.quorum = quorum;

                //I give every test run about X draws, so that the agent has a chance to get a good idea of the situation, then reset
                for(int j = 0; j < MAX_DRAWS_PER_WORLD; j++){
                    ResultValue result = world.quorumRun();
                    results[j].add(result.correct_val());
                    points[j].add(result.points());
                }
            }

            System.out.println("Test with "+agent_count+" agents is done!");

            for (int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                Double sum = results[i].stream().mapToDouble(a -> a).average().getAsDouble();
                System.out.println("Chance for correct guesses at "+i+" draws: "+sum);
                Double points_sum = points[i].stream().mapToDouble(a -> a).average().getAsDouble();
                System.out.println("Points After "+i+" draws per draw: "+points_sum);
            }
            System.out.println("This was with "+agent_count+" agents");

            System.out.println("\n\n\n");


        }



    }




    static void singleTest(){
        final int MAX_DRAWS_PER_WORLD = 250;
        int c = 0;

        //this is just stuff I added to give better output
        List<Double>[] results = new ArrayList[MAX_DRAWS_PER_WORLD];
        List<Double>[] points = new ArrayList[MAX_DRAWS_PER_WORLD];
        for (int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
            results[i] = new ArrayList<Double>();
            points[i] = new ArrayList<Double>();
        }


        do{
            if(c % 10_000 == 0) System.out.println("Setting up new world number: "+c+"\n");
            

            World world = new World();
            world.agents = new ArrayList<Agent>();
            world.agents.add(new Agent());
            Environment environment = new Environment(3.5, 5.0, 6.3, 4.0);
            world.environment = environment;

            //I give every test run about X draws, so that the agent has a chance to get a good idea of the situation, then reset
            for(int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                ResultValue result = world.testerRun(0);
                results[i].add(result.correct_val());
                points[i].add(result.points());
            }



            c++;
            if(c % 100_000 == 0){
                System.out.println("Average correct guesses after "+c+1+" worlds:");
                for(int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                    Double sum = results[i].stream().mapToDouble(a -> a).average().getAsDouble();
                    System.out.println("Chance for correct guesses at "+i+" draws: "+sum);
                    Double points_sum = points[i].stream().mapToDouble(a -> a).average().getAsDouble();
                    System.out.println("Points After "+i+" draws: "+points_sum);
                    System.out.println("This means "+points_sum/i+" points per draw");
                }
            }
        }
        while (true);

    }

}

class World{
    List<Agent> agents;
    Environment environment;
    Quorum quorum;
    public World() {
    }

    public World(List<Agent> agents) {
        this.agents = agents;
    }

    public ResultValue testerRun(int index){
        Double corret_val = .0;
        //I want to check how many times the agent guesses correctly in a certain number of guesses, before I add more information

        EnvData data = environment.nextValue();
        Situation guess = this.agents.get(index).decideOn(data);

        if(guess == data.situation()){
            corret_val = 1.0;
        }
        if(guess == Situation.DANGER && data.situation() == Situation.DANGER){
            agents.get(index).truePositive();
        }
        if(guess == Situation.DANGER && data.situation() == Situation.HARMLESS){
            agents.get(index).falsePositive();
        }

        this.agents.get(index).addMeasurement(data);
        
        return ResultValue.of(corret_val, (double)this.agents.get(0).points);
    }


    ResultValue quorumRun(){
        EnvData data = environment.nextValue();
        Situation guess = this.quorum.quorum(data);
        Double points = 0.0;
        double corret_val = guess == data.situation() ? 1.0 : 0.0;
        if ( guess == Situation.DANGER && data.situation() == Situation.DANGER){
            points = 1.0;
        }
        if ( guess == Situation.DANGER && data.situation() == Situation.HARMLESS){
            points = -1.0;
        }
        return ResultValue.of(corret_val, points);
    }



}


record ResultValue(double correct_val, double points) {

    public static ResultValue of(Double corret_val, Double points2) {
        return new ResultValue(corret_val, points2);
    }
}
