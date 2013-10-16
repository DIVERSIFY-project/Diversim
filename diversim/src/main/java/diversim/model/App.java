package diversim.model;


import java.util.List;

import sim.engine.SimState;
import diversim.strategy.Strategy;
import diversim.util.config.Configuration;


/**
 * Apps must be injected in the simulation via the createApp() method in the BipartiteGraph class.
 * 
 * @author Marco Biazzini
 */
public class App extends Entity {

double redundancy = 0;


public double getRedundancy() {
	return redundancy > 0 ? redundancy : 0;
}


public App(int id, List<Service> servs, Strategy<App> strategy) {
	super(id, strategy);
	for (Service s : servs) {
		BipartiteGraph.addUnique(services, s);
	}
}


public App() {};


@Override
public void init(String entityId, BipartiteGraph graph) {
	super.init(entityId, graph);
	int nSer = Configuration.getInt(entityId + ".services");
	for (Service s : graph.selectServices(nSer)) {
		BipartiteGraph.addUnique(services, s);
	}
}


/*
 * (non-Javadoc)
 * @see diversim.model.Entity#step(sim.engine.SimState)
 */
@SuppressWarnings("unchecked")
@Override
public void step(SimState state) {
	BipartiteGraph graph = (BipartiteGraph)state;
	strategy.evolve(graph, this);

	redundancy = ((double)degree) / graph.getNumPlatforms();
	if (redundancy > 1.0) redundancy = 1.0;
	printoutCurStep(graph);
}


@Override
public String toString() {
	String res = super.toString();
	res += " ; redundancy = " + redundancy;
	return res;
}

public boolean isAppAlive() {
	return getDegree() == getServices().size();	
}

}
