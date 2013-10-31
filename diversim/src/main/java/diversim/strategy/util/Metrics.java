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
	return graph.bipartiteNetwork.getEdges(platform, null).size() / platform.getSize();
}


public static int getPlatformPopularity(BipartiteGraph graph, Platform platform) {
	Bag uniqueEdges = graph.bipartiteNetwork.getEdges(platform, null);
	for (Object edge : graph.bipartiteNetwork.getEdges(platform, null)) {
		uniqueEdges.removeMultiply(edge);
	}
	return uniqueEdges.size();
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


public static List<List<Service>> getSpecies(BipartiteGraph graph) {
	List<List<Service>> result = new ArrayList<List<Service>>();

	return result;
}
}
