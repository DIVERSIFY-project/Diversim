package diversim.strategy;

import diversim.model.BipartiteGraph;
import sim.engine.Steppable;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:11 AM
 */
public interface Strategy<T extends Steppable> {

    /**
     * @param agent the model which is evolve
     * @param graph The bipartitegraph.
     */
    public void evolve(BipartiteGraph graph, T agent);

}
