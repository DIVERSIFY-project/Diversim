package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:33 PM
 */
public class KillPlatform  extends AbstractStrategy<Fate> {
protected KillPlatform(String n) {
	super(n);
}

public KillPlatform() {
	super("KillStrategy");
}

@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	Platform a = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
	graph.removeEntity(graph.platforms, a);

	System.out.println(graph.getPrintoutHeader() + "Fate : REMOVED " + a);
}

@Override
public void init(String stratId) {}
}