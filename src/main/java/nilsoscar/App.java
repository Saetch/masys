package nilsoscar;

import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

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
        //singleTest();
    }


    static void quorumTest(){
        final int MAX_DRAWS_PER_WORLD = 100;
        final int NUMBER_OF_AGENTS_BOUND = 250;
        final int NUMBER_OF_TRIES = 1_000;
        final boolean CHANGE_ENV = true;
        double[] overall_points = new double[NUMBER_OF_AGENTS_BOUND];
        for (int agent_count = 70; agent_count <= NUMBER_OF_AGENTS_BOUND; agent_count++){
            List<Double>[] results = new ArrayList[MAX_DRAWS_PER_WORLD*2];
            List<Double>[] points = new ArrayList[MAX_DRAWS_PER_WORLD*2];
            for (int i = 0; i < MAX_DRAWS_PER_WORLD*2; i++){
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
                for(int j = 0; j < MAX_DRAWS_PER_WORLD*2; j++){
                    if(j == MAX_DRAWS_PER_WORLD) {
                        if(CHANGE_ENV){
                            quorum.environment = new Environment(6.5, 5.0, 9.3, 4.0);
                        }
                    }
                    ResultValue result = world.quorumRun();
                    results[j].add(result.correct_val());
                    points[j].add(result.points());
                }
            }

            System.out.println("Test with "+agent_count+" agents is done!");


            double[] xValues = new double[MAX_DRAWS_PER_WORLD*2];
            double[] yValues = new double[MAX_DRAWS_PER_WORLD*2];
            for (int i = 0; i < MAX_DRAWS_PER_WORLD*2; i++){
                xValues[i] = i;
                Double sum = results[i].stream().mapToDouble(a -> a).average().getAsDouble();
                System.out.println("Chance for correct guesses at "+i+" draws: "+sum);
            
                Double points_sum = points[i].stream().mapToDouble(a -> a).average().getAsDouble();
                yValues[i] = points_sum;
                System.out.println("Points After "+i+" draws per draw: "+points_sum);
                if (i == MAX_DRAWS_PER_WORLD*2-1){
                    overall_points[agent_count-1] = points_sum;
                }
                //csv
                writeCSV("quorum_test_continuous_values_env_change_at_100_with_100_agents", xValues, yValues);
            }
            System.out.println("This was with "+agent_count+" agents");

            System.out.println("\n\n\n");

            break;

        }

        double[] xValues = new double[NUMBER_OF_AGENTS_BOUND];
        for (int i = 0; i < NUMBER_OF_AGENTS_BOUND; i++){
            xValues[i] = i;
        }
        writeCSV("quorum_test_number_of_agents_points_after_150_draws.csv", xValues, overall_points);


    }




    static void singleTest(){
        final int MAX_DRAWS_PER_WORLD = 500;
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
                if(i==400){
                    Environment new_env = new Environment(6.5, 5.0, 9.3, 4.0);
                    world.environment = new_env;
                }
                ResultValue result = world.testerRun(0);
                results[i].add(result.correct_val());
                points[i].add(result.points());
            }



            c++;
            if(c % 100_000 == 0){
                System.out.println("Average correct guesses after "+c+1+" worlds:");
                for(int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                    if(!(i % 10==0)) continue;
                    Double sum = results[i].stream().mapToDouble(a -> a).average().getAsDouble();
                    System.out.println("Chance for correct guesses at "+i+" draws: "+sum);
                    Double points_sum = points[i].stream().mapToDouble(a -> a).average().getAsDouble();
                    System.out.println("Points After "+i+" draws: "+points_sum);
                    System.out.println("This means "+points_sum/i+" points per draw");
                }
            }
            if ( c == 70_000){
                System.out.println("Writing to CSV");
                double[] xValues = new double[MAX_DRAWS_PER_WORLD];
                double[] yValues = new double[MAX_DRAWS_PER_WORLD];
                for (int i = 0; i < MAX_DRAWS_PER_WORLD; i++){
                    xValues[i] = i;
                    if(i > 1){
                        yValues[i] = points[i].stream().mapToDouble(a -> a).average().getAsDouble() - points[i-1].stream().mapToDouble(a -> a).average().getAsDouble();
                    }else{
                        yValues[i] = points[i].stream().mapToDouble(a -> a).average().getAsDouble() / ((double)i + 1.0);
                    }
                }
                writeCSV("single_test_change_env_at_400_draws.csv", xValues, yValues);
                System.out.println("Done writing to CSV");
                break;
            }
        }
        while (true);

    }


    private static void writeCSV(String filePath, double[] xValues, double[] yValues) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header if needed
            // writer.writeNext(new String[]{"X", "Y"});

            // Write data
            for (int i = 0; i < xValues.length; i++) {
                writer.writeNext(new String[]{String.valueOf(xValues[i]), String.valueOf(yValues[i])});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
