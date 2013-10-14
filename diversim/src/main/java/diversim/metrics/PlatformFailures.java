package diversim.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sim.field.network.Edge;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;

public class PlatformFailures {
	BipartiteGraph graph;

	public PlatformFailures(BipartiteGraph graph){
		this.graph = graph;
	}
	
	public double calculateWorstCaseOnePlatformFailure(){
		int numZeroApp = 0;
		Map<Platform, Integer> dep = new HashMap<Platform, Integer>();
		
		for(App app : graph.apps){
            int degree = graph.bipartiteNetwork.getEdgesOut(app).size();
			if(degree == 0)
				numZeroApp += 1;
			else if(degree == 1){
				Platform p = (Platform)((Edge) graph.bipartiteNetwork.getEdgesOut(app).get(0)).to();
				if(dep.get(p) == null)
					dep.put(p, 1);
				else
					dep.put(p, dep.get(p) + 1);
			}
				
		}
		
		int maxSinglePltf = 0;
		if(dep.size() != 0)
			maxSinglePltf = Collections.max(dep.values()).intValue();
		
		return 1 - ((double) numZeroApp + maxSinglePltf) / graph.apps.size();
	}
	
	public double calculateWorstCaseFirstAppDie(){
        
		int mindegree = graph.platforms.size();
		for(App app : graph.apps){
            int degree = graph.bipartiteNetwork.getEdgesOut(app).size();
			if(degree < mindegree)
				mindegree = degree;
        }
		return ((double) mindegree) / graph.platforms.size();
	}
}
