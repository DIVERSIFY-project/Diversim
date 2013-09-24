package diversim.strategy.reproduction;

import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;

public interface ReproductionStrategy {
	public List<Entity> reproduce(Entity parent, BipartiteGraph state);
}
