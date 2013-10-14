package diversim.strategy.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sim.util.Bag;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class Metrics {

public static double getPlatformEfficiency(BipartiteGraph graph, Platform platform) {
	return graph.bipartiteNetwork.getEdges(platform, null).size() / platform.getServices().size();
}


public static int getPlatformPopularity(BipartiteGraph graph, Platform platform) {
	Bag uniqueEdges = graph.bipartiteNetwork.getEdges(platform, null);
	for (Object edge : graph.bipartiteNetwork.getEdges(platform, null)) {
		uniqueEdges.removeMultiply(edge);
	}
	return uniqueEdges.size();
}



public static double getServicePopularity(BipartiteGraph graph, Service service) {
	double appTotal = graph.apps.size();
	double appUsingNumber = 0;
	for (App app : graph.apps) {
		if (app.getServices().contains(service)) {
			appUsingNumber++;
		}
	}
	return appUsingNumber / appTotal;
}


public static double getServiceAvailability(BipartiteGraph graph, Service service) {
	double platformTotal = graph.platforms.size();
	double platformUsingNumber = 0;
	for (Platform platform : graph.platforms) {
		if (platform.getServices().contains(service)) {
			platformUsingNumber++;
		}
	}
	return platformUsingNumber / platformTotal;
}


public static Platform getSmallestPlatform(BipartiteGraph graph) {
	Map<Integer, Platform> platformBySizeSorted = new TreeMap<Integer, Platform>();
	for (Platform p : graph.platforms) {
		platformBySizeSorted.put(p.getSize(), p);
	}
	return platformBySizeSorted.entrySet().iterator().next().getValue();
}


public static Platform getBiggestPlatform(BipartiteGraph graph) {
	Map<Integer, Platform> platformBySizeSorted = new TreeMap<Integer, Platform>();
	for (Platform p : graph.platforms) {
		// using negative size so the biggest platforms are first
		platformBySizeSorted.put(-p.getSize(), p);
	}
	return platformBySizeSorted.entrySet().iterator().next().getValue();
}


/**
 * Calculates the intra_p_div value
 * 
 * @param graph
 * @return
 */
public static double intraDiversity(BipartiteGraph graph) {
	double intraDiversity = 0;
	System.out.println(graph.platforms);
	for (Platform pi : graph.platforms) {
		for (Platform pj : graph.platforms) {
			if (pi != pj) {
			int intersectionSize = commonServices(pi, pj).size();
			intraDiversity += intersectionSize
			    / (pi.getServices().size() + pj.getServices().size() - intersectionSize);
			}
		}
	}
	return 1 - intraDiversity / (graph.getNumPlatforms() * (graph.getNumPlatforms() - 1));
}


public static List<List<Service>> getSpecies(BipartiteGraph graph) {
	List<List<Service>> result = new ArrayList<List<Service>>();

	return result;
}


/**
 * Defined as: max difference of maxDistance in size, services of the smallest Platform included in
 * the biggest Platform
 * 
 * @param p1
 * @param p2
 * @param maxDistance
 * @return
 */
public static boolean isSameSpecies(Platform p1, Platform p2, int maxDistance) {
	return Math.abs(p1.getServices().size() - p2.getServices().size()) <= maxDistance
	    && commonServices(p1, p2).size() == Math
	        .min(p1.getServices().size(), p2.getServices().size());
}


/**
 * Returns a list of the services in common between 2 Platforms
 * 
 * @param p1
 * @param p2
 * @return
 */
public static List<Service> commonServices(Platform p1, Platform p2) {
	List<Service> result = new ArrayList<Service>();
	for (Service s1 : p1.getServices()) {
		for (Service s2 : p2.getServices()) {
			if (s1 == s2 && !result.contains(s1)) {
				result.add(s1);
			}
		}
	}
	return result;
}
}
