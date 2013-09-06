package diversim.strategy.reproduction;

import java.util.List;

import diversim.Service;

/**
 *
 * @author Vivek Nallur
 *
 * This is the base interface for all speciation strategies
 */
public interface SpeciationStrategy{
        public List<Service> speciate(List<Service> current_dna, List<Service> all_services);
}

