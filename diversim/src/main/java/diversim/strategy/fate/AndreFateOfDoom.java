package diversim.strategy.fate;


import java.util.Comparator;
import java.util.List;

import sim.util.Bag;
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
	Bag platforms = new Bag(graph.platforms);
	platforms.sort(new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
			return ((Platform)o1).getSize() - ((Platform)o2).getSize();
		}

	});
	KillFates.gasFactoryExact(graph, (int)(graph.getNumServices() * 0.9), 1);
	CreationFates.splitExact(graph, 1);
	// mutation
	MutationFates.bugCorrected(graph);
	// linking
	LinkStrategyFates.linkingB(graph);
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
	  CreationFates.cloningRandom(graph, killCreateStep);
}
}
