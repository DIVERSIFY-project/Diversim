package diversim.metrics;


import diversim.model.BipartiteGraph;


public class AppFailures {

BipartiteGraph graph;

int totalAliveApps = 0;


public AppFailures(BipartiteGraph graph) {
	this.graph = graph;
}


public double calculateAliveAppsAverage() {
	totalAliveApps += graph.getAliveAppsNumber();
	return totalAliveApps / graph.getCurCycle();
}

}
