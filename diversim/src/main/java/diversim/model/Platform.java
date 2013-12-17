package diversim.model;


import sim.engine.SimState;
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


public Platform(Platform platform) {
	super((Entity)platform);
	this.pressure = platform.pressure;
	this.action = platform.action;
}


public void init(String entityId, BipartiteGraph graph) {
	super.init(entityId, graph);
	int numberServices = Math.min(
	    Configuration.getInt(entityId + ".services", graph.getNumServices()), graph.getNumServices());
	// for (Service s : graph.selectServices(numberServices)) {
	// BipartiteGraph.addUnique(services, s);
	// }
	while (numberServices > 0) {
		numberServices -= BipartiteGraph.addUnique(services, graph.selectSingleService()) >= 0 ? 1 : 0;
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
	pressure = ((double)getDegree()) / graph.getPlatformMaxLoad();
	if (pressure > 1.0) pressure = 1.0;

	// printoutCurStep(graph);
}


@Override
public String toString() {
	String res = super.toString();
	res += " ; pressure = " + pressure + " ; action = " + action;
	return res;
}

}
