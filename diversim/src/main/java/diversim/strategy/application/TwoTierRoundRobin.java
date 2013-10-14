package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.util.Bag;


/**
 * For each service, generate a random number and choose
 * a random node. If the random number is less than the 
 * chosen node's capacity, link to that node. If after 
 * 3 transfers, you still havent connected, just link
 * with previous node.
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class TwoTierRoundRobin extends AbstractStrategy<App> {

public TwoTierRoundRobin() {
	super("TwoTierRoundRobin");
}


public void evolve(BipartiteGraph graph, App e) {
	if (graph.getNumPlatforms() > 1) {
		Bag platforms = new Bag(graph.platforms);
		for (Service s: e.getServices()) {
			platforms.shuffle(graph.random);
			Platform p = null;
			Platform previous = null;
			int transfers = 0;
			do {
				int randLoad = graph.random.nextInt(graph.getPlatformMaxLoad());
				Object node = platforms.get(graph.random.nextInt(platforms.size()));
				
				if (randLoad <= ((Platform)node).getDegree() && ((Platform)node).getServices().contains(s)) {
					p = ((Platform)node);
				} else if (((Platform)node).getServices().contains(s)) {
					previous = ((Platform)node);
					transfers++;
				} else {
					transfers++;
				}
			} while (p == null && transfers < 3);
			
			if (p == null && previous != null) {
				p = previous;
			}
			
			if (p !=null) {
				graph.addEdge(e, p, e.getSize());
			}
		}
		
	}

}

@Override
public void init(String stratId) {
	this.name = "TwoTierRoundRobin";
}

}