package diversim;

import java.util.List;

/**
 *
 * @author Vivek Nallur
 *
 * This is the base interface for all speciation strategies
 */
public interface SpeciationStrategy{
        public List<Service> speciate(List<Service> current_dna, List<Service> all_services);
}

