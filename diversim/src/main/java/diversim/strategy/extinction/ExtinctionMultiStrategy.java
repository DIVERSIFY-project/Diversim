package diversim.strategy.extinction;


import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.StrategyFactory;


public class ExtinctionMultiStrategy extends ExtinctionStrategy<Entity> {


public List<ExtinctionStrategy<Entity>> killers;


protected ExtinctionMultiStrategy(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


public void initStrategies(BipartiteGraph graph) {
	this.killers = (List)StrategyFactory.fINSTANCE.createPlatformExtinctionStrategies();
}


@Override
public boolean die(Entity e, BipartiteGraph graph) {
	if (e.dead) return true;
	for (ExtinctionStrategy<Entity> killer : killers) {
		if (killer.die(e, graph)) {
			e.dead = true;
			return true;
		}
	}
	return false;
}


@Override
public void evolve(BipartiteGraph graph, Entity agent) {
	die(agent, graph);
	
}

}
