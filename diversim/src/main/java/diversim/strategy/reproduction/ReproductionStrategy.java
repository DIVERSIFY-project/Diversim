package diversim.strategy.reproduction;

import java.util.List;

import diversim.BipartiteGraph;
import diversim.Entity;
import diversim.Service;

public interface ReproductionStrategy {
	public List<Entity> reproduce(Entity parent, BipartiteGraph state);
}
