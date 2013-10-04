package diversim.strategy.extinction;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;


public class AgingExtinctionStrategy extends ExtinctionStrategy<Entity> {
	
	private Entity entity = null;
	private int expectedAge = 0;
	private long born = 0;
	
	public AgingExtinctionStrategy(Entity entity, BipartiteGraph graph, int expectedAge){
	super(""); // TODO
		this.born = graph.schedule.getSteps();		
		this.expectedAge = expectedAge;
		this.entity = entity;
		
	}


	public boolean die(BipartiteGraph graph) {
		long steps = graph.schedule.getSteps();
		if( steps - born >= expectedAge){
			return true;
		}
		else
			return false;
		
	}


	@Override
public boolean die(Entity e, BipartiteGraph graph) {
		return die(graph);
	}


@Override
public void evolve(BipartiteGraph graph, Entity agent) {
	die(agent, graph);

}

}
