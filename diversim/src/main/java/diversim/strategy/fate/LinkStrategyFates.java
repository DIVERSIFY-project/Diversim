package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sim.field.network.Edge;
import sim.util.Bag;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.util.Log;
import ec.util.MersenneTwisterFast;


public class LinkStrategyFates {

public LinkStrategyFates() {}


public static Map<String, Class[]> getLinkingMethods() {
	Map<String, Class[]> results = new HashMap<String, Class[]>();
	Class[] args = new Class[1];
	args[0] = BipartiteGraph.class;
	results.put("linkingA", args);
	args = new Class[1];
	args[0] = BipartiteGraph.class;
	results.put("linkingB", args);
	args = new Class[1];
	args[0] = BipartiteGraph.class;
	results.put("linkingC", args);
	args = new Class[1];
	args[0] = BipartiteGraph.class;
	results.put("bestFitFirst", args);
	return results;
}


public static void linkingA(BipartiteGraph graph) {
	graph.removeAllEdges();
	Bag platforms = new Bag(graph.platforms);
	platforms.shuffle(graph.random());
	List<Service> commonServices;
	for (App app : graph.apps) {
		Bag requiredServices = new Bag(app.getServices());
		for (Object platform : platforms) {
			if (((Platform)platform).getDegree() < graph.getPlatformMaxLoad()) {
				commonServices = getCommonServices((Platform)platform, requiredServices);
				if (commonServices.size() > 0) {
					graph.addEdge(app, (Platform)platform, commonServices.size());
					requiredServices.removeAll(commonServices);
				}
			}
			((Platform)platform).dead = ((Platform)platform).getDegree() == 0;
		}
		app.dead = requiredServices.size() != 0;
		if (!app.isAlive()) {
			Bag edges = new Bag();
			for (Object edge : graph.bipartiteNetwork.getEdges(app, edges)) {
				graph.removeEdge(app, (Edge)edge);
			}
		}
	}
}


public static void linkingB(BipartiteGraph graph) {
	graph.removeAllEdges();
	Bag platforms = new Bag(graph.platforms);
	platforms.sort(new Comparator<Entity>() {

		@Override
		public int compare(Entity e, Entity e2) {
			return e.getSize() - e2.getSize();
		}
	});
	List<Service> commonServices;
	for (App app : graph.apps) {
		Bag requiredServices = new Bag(app.getServices());
		for (Object platform : platforms) {
			if (((Platform)platform).getDegree() < graph.getPlatformMaxLoad()) {
				commonServices = getCommonServices((Platform)platform, requiredServices);
				if (commonServices.size() > 0) {
					graph.addEdge(app, (Platform)platform, commonServices.size());
					requiredServices.removeAll(commonServices);
				}
			}
			((Platform)platform).dead = ((Platform)platform).getDegree() == 0;
		}
		app.dead = requiredServices.size() != 0;
		if (!app.isAlive()) {
			Bag edges = new Bag();
			for (Object edge : graph.bipartiteNetwork.getEdges(app, edges)) {
				graph.removeEdge(app, (Edge)edge);
			}
		}
	}
}


public static void linkingC(BipartiteGraph graph) {
	graph.removeAllEdges();
	Bag platforms = new Bag(graph.platforms);
	platforms.sort(new Comparator<Entity>() {

		@Override
		public int compare(Entity e, Entity e2) {
			return e2.getSize() - e.getSize();
		}
	});
	List<Service> commonServices;
	for (App app : graph.apps) {
		Bag requiredServices = new Bag(app.getServices());
		for (Object platform : platforms) {
			if (((Platform)platform).getDegree() < graph.getPlatformMaxLoad()) {
				commonServices = getCommonServices((Platform)platform, requiredServices);
				if (commonServices.size() > 0) {
					graph.addEdge(app, (Platform)platform, commonServices.size());
					requiredServices.removeAll(commonServices);
				}
			}
			((Platform)platform).dead = ((Platform)platform).getDegree() == 0;
		}
		app.dead = requiredServices.size() != 0;
		if (!app.isAlive()) {
			Bag edges = new Bag();
			for (Object edge : graph.bipartiteNetwork.getEdges(app, edges)) {
				graph.removeEdge(app, (Edge)edge);
			}
		}
	}
}


public static void bestFitFirst(BipartiteGraph graph) {
	graph.removeAllEdges();
	Bag platforms = new Bag(graph.platforms);
	for (App app : graph.apps) {
		ArrayList<Service> needLinks = new ArrayList<Service>(app.getServices());
		TreeMap<Integer, Object[]> platformsSorted = sortByConnectable(graph, needLinks, platforms);
		while ((needLinks.size() > 0) && platformsSorted.size() > 0) {
			Map.Entry<Integer, Object[]> pltSet = platformsSorted.pollFirstEntry();
			Platform platform = (Platform)(pltSet.getValue())[0];
			@SuppressWarnings("unchecked")
			ArrayList<Service> removable = (ArrayList<Service>)(pltSet.getValue())[1];
			if (removable.size() > 0 && platform.getDegree() < graph.getPlatformMaxLoad()) {
				needLinks.removeAll(removable);
				graph.addEdge(app, platform, removable.size());
				platformsSorted = sortByConnectable(graph, needLinks, platforms);
			} else {
				break;
			}
		}

		app.dead = needLinks.size() != 0;
		// If app does not have all service needs fulfilled, unlink it and make it dead
		if (!app.isAlive()) {
			Bag edges = new Bag();
			for (Object edge : graph.bipartiteNetwork.getEdges(app, edges)) {
				graph.removeEdge(app, (Edge)edge);
			}
		}
		app.setRedundancy(graph);
		Log.debug("---" + app.getId() + "---" + graph.getCurCycle() + "---"
		    + graph.networkType + "---");
	}
	// graph.displayGraph();
}


/*
 * Sort a list of platforms by the number of available links it can provide out of the needed links
 */
private static TreeMap<Integer, Object[]> sortByConnectable(BipartiteGraph graph,
    ArrayList<Service> needLinks, Bag platforms) {
	TreeMap<Integer, Object[]> platformsSorted = new TreeMap<Integer, Object[]>();
	if (needLinks.size() > 0 && platforms.size() > 0) {
		for (Object p : platforms) {
			ArrayList<Service> connectableServices = BipartiteGraph.removableServices(needLinks,
			    ((Platform)p).getServices(), (graph.getPlatformMaxLoad() - ((Platform)p).getDegree()));
			// Platforms with the highest number of connectable services first
			if (connectableServices.size() > 0) {
				Object[] result = {p, connectableServices};
				platformsSorted.put(-(connectableServices.size()), result);
			}
		}
	}
	return platformsSorted;
}


public static List<Service> getCommonServices(Platform platform, Bag appServices) {
	List<Service> result = new ArrayList<Service>();
	int index = 0;
	for (Object service : appServices) {
		index = Collections.binarySearch(platform.getServices(), (Service)service);
		if (index >= 0) {
			result.add(platform.getServices().get(index));
		}
	}
	return result;
}
}
