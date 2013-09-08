package diversim.strategy;

import sim.engine.Steppable;
import diversim.model.BipartiteGraph;


/**
 * This strategy does nothing.
 * @author Marco Biazzini
 */
public class NullStrategy<T extends Steppable> extends AbstractStrategy<T> {

public NullStrategy() {
  super("null");
}

@Override
public void evolve(BipartiteGraph graph, T agent) {}

}
