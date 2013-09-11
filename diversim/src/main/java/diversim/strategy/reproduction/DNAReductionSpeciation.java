package diversim.strategy.reproduction;

import java.util.List;
import java.util.ArrayList;

import diversim.Service;
import ec.util.MersenneTwisterFast;
import sim.util.Bag;
/**
 * This class implements the Reduction speciation strategy
 * @author Vivek Nallur
 */

public class DNAReductionSpeciation implements DNASpeciation{
        public List<Service> speciate(List<Service> current_dna, List<Service> all_services){
			ArrayList<Service> current_services = new ArrayList<Service> (current_dna);
			MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
			current_services.remove(rnd.nextInt(current_services.size()));
			return new ArrayList<Service> (current_services);
        }
}
