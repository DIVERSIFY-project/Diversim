package diversim;

import java.util.*;

/**
 * An implementation of the MatchingStrategy interface, this concrete 
 * implementation returns true, if any of the services in the source, 
 * are found in the target.
 *
 * @author Vivek Nallur
 */
public class AnyMatchingService implements MatchingStrategy{
        public boolean matches(Entity source, Entity target){
                Set<Service> all_services = new HashSet<Service>(source.services);
                // Now try to add services from the target to all_services
                // If there's a single failure, then at least one service from 
                // the target matches one service in the source

                for (Service srv: target.services){
                        if(!all_services.add(srv)){
                                return true;
                        }
                }
                return false;
        }
}
