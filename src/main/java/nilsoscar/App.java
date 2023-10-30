package nilsoscar;

import java.util.ArrayList;
import java.util.List;

import nilsoscar.agents.Agent;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        World world = new World();
        world.agents = new ArrayList<Agent>();
        world.agents.add(new Agent());


        
        System.out.println( "Hello World!" );


    }

}



class World{
    List<Agent> agents;

}

