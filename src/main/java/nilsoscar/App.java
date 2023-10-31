package nilsoscar;

import java.util.ArrayList;
import java.util.List;

import nilsoscar.agents.Agent;
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

        final int MAX_DRAWS_PER_WORLD = 200;
        int c = 0;

        //this is just stuff I added to give better output
        List<Double>[] results = new ArrayList[MAX_DRAWS_PER_WORLD];
        for (int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
            results[i] = new ArrayList<Double>();
        }
        do{
            System.out.println("Setting up new world number: "+c+"\n");

            World world = new World();
            world.agents = new ArrayList<Agent>();
            world.agents.add(new Agent());
            Environment environment = new Environment(3.5, 5.0, 6.3, 4.0);
            world.environment = environment;

            //I give every test run about X draws, so that the agent has a chance to get a good idea of the situation, then reset
            for(int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                results[i].add(world.testerRun(0));
            }



            c++;
            if(c % 10 == 0){
                System.out.println("Average correct guesses after "+c+" worlds:");
                for(int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                    Double sum = results[i].stream().mapToDouble(a -> a).sum();
                    System.out.println("After "+i+" draws: "+sum/c);
                }
            }
        }
        while (true);


    }

}



class World{
    List<Agent> agents;
    Environment environment;

    public World() {
    }

    public World(List<Agent> agents) {
        this.agents = agents;
    }

    public Double testerRun(int index){
        final int RUNS  = 1_000;
        int correct_guesses = 0;
        Double corret_val;

        //I want to check how many times the agent guesses correctly in a certain number of guesses, before I add more information
        for (int i = 0; i < RUNS; i++){
            EnvData data = environment.nextValue();
            Situation guess = this.agents.get(index).decideOn(data);

            if(guess == data.reality()){
                correct_guesses++;
            }

        }
        corret_val = (double) correct_guesses / RUNS;
        
        //System.out.println("Correct guesses: " + corret_val+" with knowledge of: "+this.agents.get(index).getDangerCount()+" dangerous and "+this.agents.get(index).getHarmlessCount()+" harmless measurements");
        EnvData data_to_add = environment.nextValue();
        this.agents.get(index).addMeasurement(data_to_add);
        
        return corret_val;
    }



}

