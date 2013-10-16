package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.util.Bag;


/**
 * For each service, shuffle the platforms and then try
 * iterate through them till you find one offering the
 * service. 
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class RoundRobin extends AbstractStrategy<App> {

public RoundRobin() {
	super("RoundRobin");
}


public void evolve(BipartiteGraph graph, App e) {
	if (graph.getNumPlatforms() > 0) {
		Bag platforms = new Bag(graph.platforms);
		platforms.shuffle(graph.random);
		for (Service s: e.getServices()) {
			for (Object p : platforms) {
				if (((Platform)p).getDegree() <= graph.getPlatformMaxLoad() && ((Platform)p).getServices().contains(s)) {
					graph.addEdge(e, ((Platform)p), e.getSize());
					break;
				}
			}
		}
		
	}

}

@Override
public void init(String stratId) {
	this.name = "RoundRobin";
}

}