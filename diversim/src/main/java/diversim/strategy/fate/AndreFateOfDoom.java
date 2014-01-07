package diversim.strategy.fate;


import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;


/**
 * User: Andre Date: 26/9/13 Time: 10:45 AM
 */
public class AndreFateOfDoom extends AbstractStrategy<Fate> {

public AndreFateOfDoom() {
	super("Andre");
}


@SuppressWarnings("unchecked")
@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	// managePopulation(graph);
	/*
	 * Bag platforms = new Bag(graph.platforms); platforms.sort(new Comparator() {
	 * @Override public int compare(Object o1, Object o2) { return ((Platform)o1).getSize() -
	 * ((Platform)o2).getSize(); } });
	 */
	// KillFates.serveOrDie(graph);
	// KillFates.gasFactory(graph, (int)(graph.getNumServices() * 0.9), 0.1);
	//
	KillFates.concentrationRandom(graph, 0.14);
	// KillFates.random(graph, 0.15);
	CreationFates.split(graph, 0.9, 0.15);
	// mutation
	// MutationFates.bugCorrected(graph);
	//
	MutationFates.random(graph, 0.1, 0.1);
	// linking
	// LinkStrategyFates.linkingC(graph);
	// LinkStrategyFates.bestFitFirst(graph);
	for (App app : graph.apps) {
		app.step(graph);
	}
	for (Platform platform : graph.platforms) {
		platform.step(graph);
	}
}


public void managePopulation(BipartiteGraph graph) {
	double killCreateStep = 0.1;
	double killThreshold = 0.3;
	double createThreshold = 0.3;
	double growthStep = 0.1;
	double growthMargin = 0.5;
	if (graph.getNumPlatforms() <= (1 + growthMargin) * graph.getInitPlatforms()
	    && graph.getNumPlatforms() >= (1 - growthMargin) * graph.getInitPlatforms()) {
		killThreshold = 0.3;
		createThreshold = 0.3;
	}
	if (graph.getNumPlatforms() > (1 + growthMargin) * graph.getInitPlatforms()) {
		killThreshold -= growthStep;
		createThreshold += growthStep;
	}
	if (graph.getNumPlatforms() < (1 - growthMargin) * graph.getInitPlatforms()) {
		killThreshold += growthStep;
		createThreshold -= growthStep;
	}
	// killing
	if (graph.random().nextDouble() > killThreshold) KillFates.random(graph, killCreateStep);
	// creation
	if (graph.random().nextDouble() > createThreshold)
	// TODO
	  return;
	// CreationFates.cloningRandom(graph, killCreateStep);
}
}
