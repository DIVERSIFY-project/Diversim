package diversim;

import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;

public class ReConnect implements Steppable {
	MatchingStrategy matcher = new AllMatchingService(); 

	@Override
	public void step(SimState state) {
		BipartiteGraph graph = (BipartiteGraph) state;
		
		List<App> dead = new ArrayList<App>();
		for(App a : graph.apps){
			if(a.dead){
				dead.add(a);
				graph.bipartiteNetwork.removeNode(a);
				//graph.schedule
			}
		}
		
		System.out.println(String.format("apps: %d",graph.apps.size()));
		graph.apps.removeAll(dead);
		
		
		System.out.println(String.format("apps: %d",graph.apps.size()));
		
		
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
