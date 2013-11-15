package diversim.strategy.extinction;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;


public class AgingExtinctionStrategy extends ExtinctionStrategy<Entity> {
	
protected int expectedAge;

	
public AgingExtinctionStrategy() {
	super("aging"); // TODO

}


public boolean die(Entity e, BipartiteGraph graph) {
	long steps = graph.getCurCycle();
	if (steps - e.getBirthCycle() >= expectedAge) {
			return true;
		}
		else
			return false;
		
	}


@Override
public void evolve(BipartiteGraph graph, Entity agent) {
	agent.dead = die(agent, graph);

}

}
