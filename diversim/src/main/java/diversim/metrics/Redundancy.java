package diversim.metrics;


import java.util.HashSet;
import java.util.Set;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;

/**
 * Platform Redundancy is the ratio of all the effective services between the 
 * total number of services. In another word, it is 1- |\cap p_i.s| / |\cup p_i.s|
 * 
 * The "platform redundancy to apps" mans the ratio between the effective
 * connection and the total number of connections.
 * 
 * @author Hui Song
 */
public class Redundancy {
	
	BipartiteGraph graph = null;
	
	public Redundancy(BipartiteGraph graph){
		this.graph = graph;
	}
	
	public double calculatePlatformRedundancyToApps(){
		int nSupportedApps = 0;
		int nConnect = 0;
		
		for(App app : graph.apps){
            int degree = graph.bipartiteNetwork.getEdgesOut(app).size();
			if(degree > 0){
				nSupportedApps += 1;
				nConnect += degree;
			}
		}
		if(nConnect == 0){
			return 1;
		}
		else{
			return 1 - ((double)nSupportedApps) / nConnect; 
		}
	}
	
	public double calculatePlatformRedundancy(){
		Set<Service> allSupported = new HashSet<Service>();
		int totalNumber = 0;
		
		for(Platform p : graph.platforms){
			allSupported.addAll(p.services);
			totalNumber += p.services.size();
		}
		
		if(totalNumber == 0)
			return 1;
		return 1 - ((double)allSupported.size()) / totalNumber;
	}
}
