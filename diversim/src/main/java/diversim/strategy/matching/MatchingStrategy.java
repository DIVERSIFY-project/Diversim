package diversim.strategy.matching;

import diversim.Entity;

/**
 * This interface defines the basic method for determining whether an entity 
 * matches another. A concrete sub-class is expected to define a particular 
 * context in which one entity decides that another entity provides what it 
 * requires. For example, an application may decide that a platform 'matches', 
 * if it provides at least one of the services required by the application. Or 
 * it may decide that a platform only matches if it provides *all* the services 
 * required by the application. This, obviously, is dependent on the concrete 
 * implementation of the strategy.
 */

public interface MatchingStrategy{
        public boolean matches(Entity source, Entity target);
}
