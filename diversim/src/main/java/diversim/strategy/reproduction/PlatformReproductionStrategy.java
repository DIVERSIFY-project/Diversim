package diversim.strategy.reproduction;

import java.util.List;

import diversim.BipartiteGraph;
import diversim.Platform;
import ec.util.MersenneTwisterFast;

/**
 * This interface must be implemented by all reproduction strategies of Platform
 * @author Vivek Nallur
 */
public interface PlatformReproductionStrategy{
        public List<Platform> reproduce(Platform parent, BipartiteGraph state);
}
