package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.util.Bag;

import java.util.Comparator;


/**
 * Assign to most powerful nodes first 
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class MacroFairShare extends AbstractStrategy<App> {

public MacroFairShare() {
	super("MacroFairShare");
}


public void evolve(BipartiteGraph graph, App e) {
	if (graph.getNumPlatforms() > 0) {
		Bag platforms = new Bag(graph.platforms);
		for (Service s: e.getServices()) {
			platforms.sort(new Comparator<Entity>() {

				@Override
				public int compare(Entity e, Entity e2) {
					return e.getSize() - e2.getSize();
				}
			});
			platforms.reverse();
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
	this.name = "MacroFairShare";
}

}