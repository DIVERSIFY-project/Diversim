package diversim.strategy.fate;


import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;


/**
 * User: Andre Date: 26/9/13 Time: 10:45 AM
 */
public class Fate3 extends AbstractStrategy<Fate> {

public Fate3() {
	super("3");
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	KillFates.concentrationRandom(graph, 0.14);
	CreationFates.split(graph, 0.9, 0.15);
	MutationFates.random(graph, 0.1, 0.1);
	MutationFates.increaseOffer(graph, 0.1, 0.1);
	for (App app : graph.apps) {
		app.step(graph);
	}
	for (Platform platform : graph.platforms) {
		platform.step(graph);
	}
}
}
