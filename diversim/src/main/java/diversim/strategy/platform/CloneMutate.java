package diversim.strategy.platform;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.Strategy;
import sim.field.network.Edge;
import sim.util.Bag;

import java.util.ArrayList;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:21 AM
 */
public class CloneMutate implements Strategy<Platform> {
    @Override
    public void evolve(BipartiteGraph graph, Platform platform) {
        clone_Mutate(graph,platform);
    }

    /**
     * Clone this instance and mutate both this instance and the clone, so that
     * one randomly chosen service in each instance (not the same in both) is removed.
     * Then update the network so that the apps link to the proper instance(s).
     *
     * @param graph
     */
    private void clone_Mutate(BipartiteGraph graph, Platform platform) {
        int r1, r2;
        Bag out = graph.bipartiteNetwork.getEdges(this, null); // read-only!
        Edge[] edges = (Edge[]) out.toArray(new Edge[0]);
        ArrayList<Entity> ents = new ArrayList<Entity>();
        for (Edge e : edges) {
            ents.add((Entity) e.getOtherNode(this));
        }

        r1 = graph.random.nextInt(platform.getServices().size());
        do
            r2 = graph.random.nextInt(platform.getServices().size());
        while (r1 == r2);

        // generate a clone that has all the services of this platform but one.
        ArrayList<Service> servs = new ArrayList<Service>(platform.getServices());
        servs.remove(r2);
        Platform p = graph.createPlatform(servs);

        // remove a service from this platform
        platform.getServices().remove(r1);
    }
}
