package diversim;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import ec.util.MersenneTwisterFast;
import sim.util.Bag;
/**
 * This class implements the Extension speciation strategy
 * @author Vivek Nallur
 */

public class ExtensionSpeciation implements SpeciationStrategy{
        public List<Service> speciate(List<Service> current_dna, List<Service> all_services){
		Set<Service> current_services = new HashSet<Service>(current_dna);
		Service new_service;
		MersenneTwisterFast rnd = new MersenneTwisterFast();
		do {
			new_service = all_services.get(rnd.nextInt(all_services.size()));
		}while(!current_services.add(new_service));
		return new ArrayList<Service> (current_services);
        }
}
