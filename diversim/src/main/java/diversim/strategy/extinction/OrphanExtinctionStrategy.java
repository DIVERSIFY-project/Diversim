package diversim.strategy.extinction;

import diversim.BipartiteGraph;
import diversim.Entity;

public class OrphanExtinctionStrategy implements ExtinctionStrategy {
	
	private Entity entity = null;

	
	public OrphanExtinctionStrategy(Entity entity){
		
		this.entity = entity;
		
	}

	@Override
	public boolean die(BipartiteGraph graph) {
		if(entity.services.size() == 0)
			return true;
		if(entity.degree == 0)
			return true;
		return false;
	}

}
