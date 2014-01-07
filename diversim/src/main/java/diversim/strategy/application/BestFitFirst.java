package diversim.strategy.application;


import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import diversim.util.Log;
import sim.field.network.Edge;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import sim.util.Bag;


/**
 * Sort platforms by common services Connect to platforms in that order until all services fulfilled
 * If not all services fulfilled, unlink app
 * 
 * @author Kwaku Yeboah-Antwi
 */

public class BestFitFirst extends AbstractStrategy<App> {

public BestFitFirst() {
	super("BestFitFirst");
}


public void evolve(BipartiteGraph graph, App a) {
	ArrayList<Service> needLinks = new ArrayList<Service>(a.getServices());
	Bag platforms = new Bag(graph.platforms);
	TreeMap<Integer, Object[]> platformsSorted = sortByConnectable(graph, needLinks, platforms);

	while ((needLinks.size() > 0) && platformsSorted.size() > 0) {
		Map.Entry<Integer, Object[]> pltSet = platformsSorted.pollFirstEntry();
		Platform p = (Platform)(pltSet.getValue())[0];
		@SuppressWarnings("unchecked")
		ArrayList<Service> removable = (ArrayList<Service>)(pltSet.getValue())[1];
		if (removable.size() > 0) {
			needLinks.removeAll(removable);
			graph.addEdge(a, p, removable.size());
			platformsSorted = sortByConnectable(graph, needLinks, platforms);
		} else {
			break;
		}
	}

	a.dead = needLinks.size() != 0;
	// If app does not have all service needs fulfilled, unlink it and make it dead
	if (!a.isAlive()) {
		// indirected network: no edgesOut
		for (Object edge : graph.bipartiteNetwork.getEdgesIn(a)) {
			graph.removeEdge(a, (Edge)edge);
		}
	}
	a.setRedundancy(graph);
	Log.debug("---" + a.getId() + "---" + graph.getCurCycle() + "---" + graph.networkType + "---");
	// graph.displayGraph();
}


/*
 * Sort a list of platforms by the number of available links it can provide out of the needed links
 */
private TreeMap<Integer, Object[]> sortByConnectable(BipartiteGraph graph,
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


@Override
public void init(String stratId) {
	this.name = "BestFitFirst";
}

}