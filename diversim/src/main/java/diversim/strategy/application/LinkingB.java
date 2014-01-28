package diversim.strategy.application;

import java.util.Comparator;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.field.network.Edge;
import sim.util.Bag;


/**
 * Sort platforms in ascending order. 
 * Scan the list of services 
 * Add edge every time you find a platform 
 * that can support that service
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class LinkingB extends AbstractStrategy<App> {

public LinkingB() {
	super("LinkingB");
}


public void evolve(BipartiteGraph graph, App e) {
	//Remove all links to app
	// Bag edges = graph.bipartiteNetwork.getEdgesIn(e);
	Bag edges = new Bag();
	graph.bipartiteNetwork.getEdges(e, edges);
	for (Object edge : edges) {
		graph.removeEdge(e, (Edge)edge);
	}
	
	//If there are platforms, try and link to them
	if (graph.getNumPlatforms() > 0) {
		Bag platforms = new Bag(graph.platforms);
		//Sort platform in ascending order
		platforms.sort(new Comparator<Entity>() {

			@Override
			public int compare(Entity e, Entity e2) {
				return e.getDegree() - e2.getDegree();
			}
		});
		
		Bag needLinks = new Bag(e.getServices());
		for (Object s : needLinks) {
			for (Object p : platforms) {
				if (((Platform)p).getDegree() <= graph.getPlatformMaxLoad() && ((Platform)p).getServices().contains(s)) {
					graph.addEdge(e, ((Platform)p), ((Platform)p).countCommonServices(e, null));
					break;
				}
			}
		}
		
	}

}


@Override
public void init(String stratId) {
	this.name = "LinkingB";
}

}