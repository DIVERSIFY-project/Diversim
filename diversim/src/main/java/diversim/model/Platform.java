package diversim.model;

import java.util.List;

import diversim.strategy.Strategy;
import sim.engine.SimState;


/**
 * Platforms are agents pro-actively modifying their state according to
 * some diversification rules to be included in their step() method.
 * By modifying their state they may also affect the network topology,
 * which should be updated accordingly.
 *
 * @author Marco Biazzini
 */
public class Platform extends Entity {

int maxLoad; // TODO
int minLoad; // TODO
int minSize; // TODO
int maxSize; // TODO

    double pressure;
    public String action;


    public double getPressure() {
        return pressure;
    }

    public String getAction() {
        return action;
    }


    public Platform(int id, List<Service> servs, Strategy<Platform> strategy) {
        super(id,strategy);
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
    @Override
    public void step(SimState state) {
        BipartiteGraph graph = (BipartiteGraph) state;
        action = "none";
        if (getDegree() >= graph.getPlatformMaxLoad()
            && getSize() > graph.getPlatformMinSize()) {
        strategy.evolve(graph, this);
        }
        pressure = ((double) degree) / graph.getPlatformMaxLoad();
        if (pressure > 1.0) pressure = 1.0;

        System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
    }


    @Override
    public String toString() {
        String res = super.toString();
        res += " ; pressure = " + pressure
                + " ; action = " + action;
        return res;
    }

}
