package diversim.strategy.extinction;


import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.strategy.AbstractStrategy;


public abstract class ExtinctionStrategy<T extends Entity> extends AbstractStrategy<T> {

protected ExtinctionStrategy(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


@Override
abstract public void evolve(BipartiteGraph graph, T agent);


abstract public boolean die(T entity, BipartiteGraph graph);

}
