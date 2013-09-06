package diversim.strategy.extinction;

import diversim.BipartiteGraph;
import diversim.Entity;

public class AgingExtinctionStrategy implements ExtinctionStrategy {
	
	private Entity entity = null;
	private int expectedAge = 0;
	private long born = 0;
	
	public AgingExtinctionStrategy(Entity entity, long born, int expectedAge){
		
		this.born = born;		
		this.expectedAge = expectedAge;
		this.entity = entity;
		
	}

	@Override
	public boolean die(BipartiteGraph graph) {
		long steps = graph.schedule.getSteps();
		if( steps - born >= expectedAge){
			return true;
		}
		else 
			return false;
		
	}

}
