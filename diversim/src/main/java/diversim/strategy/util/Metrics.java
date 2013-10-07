package diversim.strategy.util;


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
}
