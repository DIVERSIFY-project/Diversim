package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.field.network.Edge;
import sim.util.Bag;


/**
 * Randomly shuffle all platforms. 
 * Scan the list of services 
 * Add edge every time you find a platform 
 * with that can support that service
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class LinkingA extends AbstractStrategy<App> {

public LinkingA() {
	super("LinkingA");
}


public void evolve(BipartiteGraph graph, App app) {
	//Remove all links to app
	Bag edges = graph.bipartiteNetwork.getEdgesIn(app);
	for (Object edge : edges) {
		graph.removeEdge(app, (Edge)edge);
	}
	
	//If there are platforms, try and link to them
	if (graph.getNumPlatforms() > 0) {
		Bag platforms = new Bag(graph.platforms);
		platforms.shuffle(graph.random);
		Bag needLinks = new Bag(app.getServices());
		for (Object s : needLinks) {
			for (Object p : platforms) {
				if (((Platform)p).getDegree() <= graph.getPlatformMaxLoad()
				    && ((Platform)p).getServices().contains(s)) {
					graph.addEdge(app, ((Platform)p),
					    new Integer(((Platform)p).countCommonServices(app, null)));
					break;
				}
			}
		}
		
	}

}


@Override
public void init(String stratId) {
	this.name = "LinkingA";
}

}