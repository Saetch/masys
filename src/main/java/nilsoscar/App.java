package nilsoscar;

import nilsoscar.HelperClasses.NormalDistributionHelper;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        NormalDistributionHelper dist1 = new NormalDistributionHelper(10, 1.5);
        for (int i = 0; i < 1000; i++){
            System.out.println(dist1.getValue());
        }
    }
}
