package diversim;

import java.util.ArrayList;
import java.util.List;

import diversim.strategy.matching.AllMatchingService;
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
		
		List<App> dead = new ArrayList<App>();
		for(App a : graph.apps){
			if(a.dead){
				dead.add(a);
				graph.bipartiteNetwork.removeNode(a);
				
			}
		}
		
		graph.apps.removeAll(dead);
		
		List<Platform> deadp = new ArrayList<Platform>();
		for(Platform p : graph.platforms){
			if(p.dead){
				deadp.add(p);
				graph.bipartiteNetwork.removeNode(p);
			}
		}
		
		graph.platforms.removeAll(deadp);
		System.out.println(String.format("Step %d : apps: %d, platforms: %d", 
				graph.schedule.getSteps(), graph.apps.size(), graph.platforms.size()));
		
		
		
		
		for(App app:graph.apps)
			app.degree = 0;
		for(Platform pltf : graph.platforms)
			pltf.degree = 0;
		
		graph.bipartiteNetwork.removeAllEdges();
		
		for(App app : graph.apps)
			for(Platform pltf : graph.platforms)
				if(matcher.matches(app, pltf)){
					graph.setLink(app, pltf);
				}
			
		
	}
	
	

}
