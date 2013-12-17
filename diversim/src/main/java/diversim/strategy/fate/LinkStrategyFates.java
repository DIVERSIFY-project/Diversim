package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sim.field.network.Edge;
import sim.util.Bag;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;
import diversim.model.Service;
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
			// indirected network: no edgesOut
			for (Object edge : graph.bipartiteNetwork.getEdgesIn(app)) {
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
			// indirected network: no edgesOut
			for (Object edge : graph.bipartiteNetwork.getEdgesIn(app)) {
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
			// indirected network: no edgesOut
			for (Object edge : graph.bipartiteNetwork.getEdgesIn(app)) {
				graph.removeEdge(app, (Edge)edge);
			}
		}
	}
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