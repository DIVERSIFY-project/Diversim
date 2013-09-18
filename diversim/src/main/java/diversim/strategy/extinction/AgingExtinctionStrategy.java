package diversim.strategy.extinction;

import diversim.App;
import diversim.BipartiteGraph;
import diversim.Entity;
import diversim.Platform;

public class AgingExtinctionStrategy implements AppExtinctionStrategy, PlatformExtinctionStrategy {
	
	private Entity entity = null;
	private int expectedAge = 0;
	private long born = 0;
	
	public AgingExtinctionStrategy(Entity entity, BipartiteGraph graph, int expectedAge){
		
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
	public boolean die(Platform platform, BipartiteGraph graph) {
		return die(graph);
	}


	@Override
	public boolean die(App app, BipartiteGraph graph) {
		// TODO Auto-generated method stub
		return die(graph);
	}

}
