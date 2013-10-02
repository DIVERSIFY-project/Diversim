package diversim.model;

import diversim.strategy.matching.MatchingStrategy;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * ReConnect-ing happens before the re-drawing of the GUI, which remove dead
 * Apps and Platforms, and calculate the links between them based on the matching
 * Strategies.
 *
 * @author Hui Song
 *
 */
public class ReConnect implements Steppable {
	MatchingStrategy matcher = StrategyFactory.fINSTANCE.createMatchingStrategy();

	@Override
	public void step(SimState state) {
		BipartiteGraph graph = (BipartiteGraph) state;

	App a;
	for (int i = graph.getNumApps() - 1; i >= 0; i--) {
		a = graph.apps.get(i);
		if (a.dead) {
			graph.removeEntity(graph.apps, a);
		}
	}

	Platform p;
	for (int i = graph.getNumPlatforms() - 1; i >= 0; i--) {
		p = graph.platforms.get(i);
		if (p.dead) {
			graph.removeEntity(graph.platforms, p);
		}
	}

		System.out.println(String.format("Step %d : apps: %d, platforms: %d",
				graph.schedule.getSteps(), graph.apps.size(), graph.platforms.size()));

	graph.removeAllEdges();
		
		for(App app : graph.apps)
			for(Platform pltf : graph.platforms)
				if(matcher.matches(app, pltf)){
					graph.addEdge(app, pltf, new Integer(1));
				}
			
		
	}
	
	

}
