package diversim.strategy.matching;

import java.util.*;

import diversim.Entity;
import diversim.Service;

/**
 * An implementation of the MatchingStrategy interface, this concrete 
 * implementation returns true only if all of the services required by the 
 * source, are found in the target. That is, if source is a subset of target.
 *
 * @author Vivek Nallur
 */
public class AllMatchingService implements MatchingStrategy{
        public boolean matches(Entity source, Entity target){
                Set<Service> target_services = new HashSet<Service>(target.services);
                if (target_services.containsAll(new HashSet<Service>(source.services))){
                        return true;
                }
                return false;
        }
}
