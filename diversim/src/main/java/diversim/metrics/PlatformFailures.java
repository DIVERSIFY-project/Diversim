package diversim.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sim.field.network.Edge;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;
import java.util.ArrayList;
import java.util.Collection;

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

    public double toSupportNewApp() {
        int succeed = 0;
        int size = graph.random().nextInt(19) + 1;
        for (int i = 0; i < size; i++) {
            Collection<Service> toSupport = graph.selectServices(0);
            for (Platform p : graph.platforms) {
                if (p.services.containsAll(toSupport)) {
                    succeed += 1;
                    break;
                }

            }
        }
        return  ((double)succeed) / size;
    }

    public double farFromSupportingNewApp() {
        int totalDistance = 0;
        int size = graph.random().nextInt(19) + 1;
        for (int i = 0; i < size; i++) {
            int distance = 10000;
            Collection<Service> toSupport = graph.selectServices(0);
            ArrayList<Service> copyToSupport = new ArrayList<Service>();
            for (Platform p : graph.platforms) {
                copyToSupport.clear();
                copyToSupport.addAll(toSupport);
                copyToSupport.removeAll(p.services);
                int unsupported = copyToSupport.size();
                if (unsupported < distance) {
                    distance = unsupported;
                }
                if (distance == 0) {
                    
                    break;
                }
            }
            totalDistance += distance;
        }
        return ( (double)totalDistance) /size;
    }
}
