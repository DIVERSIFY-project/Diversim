package diversim.strategy.fate;


import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;


/**
 * User: Andre Date: 26/9/13 Time: 10:45 AM
 */
public class Fate2 extends AbstractStrategy<Fate> {

public Fate2() {
	super("2");
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	KillFates.concentrationRandom(graph, 0.05);
	CreationFates.split(graph, 0.9, 0.06);
	MutationFates.random(graph, 0.2, 0.2);
	for (App app : graph.apps) {
		app.step(graph);
	}
	for (Platform platform : graph.platforms) {
		platform.step(graph);
	}
}
}
