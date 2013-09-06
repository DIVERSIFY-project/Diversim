package diversim.strategy.extinction;

import diversim.BipartiteGraph;

/**
 * The interface for extinction strategies
 * @author Vivek Nallur
 */
public interface ExtinctionStrategy {
        public boolean die(BipartiteGraph graph);
}
