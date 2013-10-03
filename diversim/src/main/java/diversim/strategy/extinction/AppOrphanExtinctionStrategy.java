package diversim.strategy.extinction;

import diversim.model.App;
import diversim.model.BipartiteGraph;

public class AppOrphanExtinctionStrategy implements AppExtinctionStrategy {
	
	
	@Override
	public boolean die(App app, BipartiteGraph graph) {
		if(app.services.size() == 0)
			return true;
	if (app.getDegree() == 0)
			return true;
		return false;
	}

}
