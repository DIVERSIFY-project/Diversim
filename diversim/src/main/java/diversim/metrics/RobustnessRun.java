package diversim.metrics;


import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import diversim.model.BipartiteGraph;


public class RobustnessRun implements Callable<RobustnessResults> {

BipartiteGraph graph;

Method linkingMethod;

Method killingMethod;

int totalNumStrategies;

int currentStrategyIndex;


public RobustnessRun(BipartiteGraph graph, Method linkingMethod, Method killingMethod,
    int totalNumStrategies, int currentStrategyIndex) {
	this.graph = graph;
	this.linkingMethod = linkingMethod;
	this.killingMethod = killingMethod;
	this.totalNumStrategies = totalNumStrategies;
	this.currentStrategyIndex = currentStrategyIndex;
}


@Override
public RobustnessResults call() {
	return Robustness.calculateRobustness(graph, linkingMethod, killingMethod, totalNumStrategies,
	    currentStrategyIndex);
}

}
