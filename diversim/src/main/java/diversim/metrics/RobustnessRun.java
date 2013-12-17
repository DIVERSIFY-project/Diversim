package diversim.metrics;


import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import diversim.model.BipartiteGraph;


public class RobustnessRun implements Callable<RobustnessResults> {

BipartiteGraph graph;

Method linkingMethod;

Method killingMethod;


public RobustnessRun(BipartiteGraph graph, Method linkingMethod, Method killingMethod) {
	this.graph = graph;
	this.linkingMethod = linkingMethod;
	this.killingMethod = killingMethod;
}


@Override
public RobustnessResults call() {
	return Robustness.calculateRobustness(graph, linkingMethod, killingMethod);
}

}
