package diversim.strategy.reproduction;

import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.strategy.Strategy;


/**
 * This interface must be implemented by all reproduction strategies of Platform
 * @author Vivek Nallur
 */
public interface PlatformReproductionStrategy extends Strategy<Platform> {
        public List<Platform> reproduce(Platform parent, BipartiteGraph state);
}
