package diversim.strategy.extinction;

import diversim.model.App;
import diversim.model.BipartiteGraph;


public class AppOrphanExtinctionStrategy extends ExtinctionStrategy<App> {
	
	
public AppOrphanExtinctionStrategy(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


	@Override
	public boolean die(App app, BipartiteGraph graph) {
		if(app.services.size() == 0)
			return true;
	if (app.getDegree() == 0)
			return true;
		return false;
	}


@Override
public void evolve(BipartiteGraph graph, App agent) {
	agent.dead = die(agent, graph);

}

}
