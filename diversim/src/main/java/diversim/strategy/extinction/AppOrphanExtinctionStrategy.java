package diversim.strategy.extinction;

import diversim.App;
import diversim.BipartiteGraph;
import diversim.Entity;

public class AppOrphanExtinctionStrategy implements AppExtinctionStrategy {
	
	
	@Override
	public boolean die(App app, BipartiteGraph graph) {
		if(app.services.size() == 0)
			return true;
		if(app.degree == 0)
			return true;
		return false;
	}

}
