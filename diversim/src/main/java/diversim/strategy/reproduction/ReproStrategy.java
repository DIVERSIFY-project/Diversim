package diversim.strategy.reproduction;

import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.strategy.AbstractStrategy;


public abstract class ReproStrategy<T extends Entity> extends AbstractStrategy<T> {

protected ReproStrategy(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


abstract public List<T> reproduce(T parent, BipartiteGraph state);


@Override
abstract public void evolve(BipartiteGraph state, T parent);

}
