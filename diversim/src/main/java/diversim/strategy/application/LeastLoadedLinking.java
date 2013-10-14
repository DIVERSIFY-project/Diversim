package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.util.Bag;

import java.util.Comparator;


/**
 * For each service, sort the platforms least loaded first 
 * and then try iterate through them till you find one 
 * offering the service. 
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class LeastLoadedLinking extends AbstractStrategy<App> {

public LeastLoadedLinking() {
	super("LeastLoadedLinking");
}


public void evolve(BipartiteGraph graph, App e) {
	if (graph.getNumPlatforms() > 0) {
		Bag platforms = new Bag(graph.platforms);
		for (Service s: e.getServices()) {
			platforms.sort(new Comparator<Entity>() {

				@Override
				public int compare(Entity e, Entity e2) {
					return e.getDegree() - e2.getDegree();
				}
			});
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
	this.name = "LeastLoadedLinking";
}

}