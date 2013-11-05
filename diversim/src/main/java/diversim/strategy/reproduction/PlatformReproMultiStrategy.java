package diversim.strategy.reproduction;


import java.util.ArrayList;
import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.StrategyFactory;
import diversim.strategy.AbstractStrategy;


public class PlatformReproMultiStrategy extends AbstractStrategy<Platform> {

List<ReproStrategy<Platform>> reproducers;

protected PlatformReproMultiStrategy(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


public void initStrategies(BipartiteGraph graph) {
	this.reproducers = (List)StrategyFactory.fINSTANCE.createPlatformReproductionStrategy();
}


public List<Platform> reproduce(Platform p, BipartiteGraph state) {
	List<Platform> result = new ArrayList<Platform>();
	for (ReproStrategy<Platform> reproducer : reproducers) {
		result.addAll(reproducer.reproduce(p, state));
	}
	return result;
}


@Override
public void evolve(BipartiteGraph graph, Platform agent) {
	reproduce(agent, graph);
}

}
