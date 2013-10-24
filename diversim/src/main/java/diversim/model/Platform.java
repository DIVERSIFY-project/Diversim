package diversim.model;


import java.util.List;

import sim.engine.SimState;
import diversim.strategy.Strategy;
import diversim.util.config.Configuration;


/**
 * Platforms are agents pro-actively modifying their state according to some diversification rules
 * to be included in their step() method. By modifying their state they may also affect the network
 * topology, which should be updated accordingly.
 * 
 * @author Marco Biazzini
 */
public class Platform extends Entity {

double pressure;

public String action;


public double getPressure() {
	return pressure;
}


public String getAction() {
	return action;
}


public Platform() {}


public void init(String entityId, BipartiteGraph graph) {
	super.init(entityId, graph);
	int numberServices = Math.min(
	    Configuration.getInt(entityId + ".services", graph.getNumServices()), graph.getNumServices());
	// for (Service s : graph.selectServices(numberServices)) {
	// BipartiteGraph.addUnique(services, s);
	// }
	while (numberServices > 0) {
		numberServices += BipartiteGraph.addUnique(services, graph.selectSingleService()) >= 0 ? -1 : 0;
	}
	pressure = 0;
	action = "none";
}


public Platform(int id, List<Service> servs, Strategy<Platform> strategy) {
	super(id, strategy);
	for (Service s : servs) {
		BipartiteGraph.addUnique(services, s);
	}
	pressure = 0;
	action = "none";
}


/*
 * (non-Javadoc)
 * @see diversim.model.Entity#step(sim.engine.SimState)
 */
@SuppressWarnings("unchecked")
@Override
public void step(SimState state) {
	BipartiteGraph graph = (BipartiteGraph)state;
	action = "none";
	if (getDegree() >= graph.getPlatformMaxLoad() && getSize() > graph.getPlatformMinSize()) {
		strategy.evolve(graph, this);
	}
	pressure = ((double)degree) / graph.getPlatformMaxLoad();
	if (pressure > 1.0) pressure = 1.0;

	printoutCurStep(graph);
}


@Override
public String toString() {
	String res = super.toString();
	res += " ; pressure = " + pressure + " ; action = " + action;
	return res;
}

}
