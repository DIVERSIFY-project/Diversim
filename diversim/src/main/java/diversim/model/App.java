package diversim.model;


import java.util.List;

import diversim.strategy.Strategy;
import sim.engine.SimState;

/**
 * Apps must be injected in the simulation via the createApp() method in the BipartiteGraph class.
 *
 * @author Marco Biazzini
 */
public class App extends Entity {

    double redundancy = 0;


    public double getRedundancy() {
        return redundancy;
    }


    public App(int id, List<Service> servs, Strategy strategy) {
        super(id,strategy);
        for (Service s : servs) {
            BipartiteGraph.addUnique(services, s);
        }
    }


    /*
     * (non-Javadoc)
     * @see fr.inria.diversim.model.Entity#step(sim.engine.SimState)
     */
    @Override
    public void step(SimState state) {
        BipartiteGraph graph = (BipartiteGraph) state;
        strategy.evolve(graph,this);

        redundancy = ((double) degree) / graph.numPlatforms;
        System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
    }

    @Override
    public String toString() {
        String res = super.toString();
        res += " ; redundancy = " + redundancy;
        return res;
    }

}