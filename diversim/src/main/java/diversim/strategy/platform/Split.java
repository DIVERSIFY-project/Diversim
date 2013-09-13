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
public class Split implements Strategy<Platform> {
    @Override
    public void evolve(BipartiteGraph graph, Platform platform) {
        split_Part(graph,platform);
    }


    /**
     * Split this platform in two and partition the services, so that
     * the most common services among the linked application are kept in this
     * instance and the other half of the services are removed and assigned
     * to the newly created platform.
     *
     * @param graph
     */
    private void split_Part(BipartiteGraph graph, Platform platform) {
        Bag out = graph.bipartiteNetwork.getEdges(this, null); // read-only!
        Edge[] edges = (Edge[])out.toArray(new Edge[0]);
        // get the services used by the apps, sorted from the most to the least common
        ArrayList<Service> sortedServices = platform.sortServices(out);

        // split the platform and keep here only the most shared half of the services
        Platform p = graph.createPlatform(
                sortedServices.subList(sortedServices.size() / 2, sortedServices.size()));
        ArrayList<Entity> ents = new ArrayList<Entity>();
        for (Edge e : edges) {
            ents.add((Entity)e.getOtherNode(this));
        }
        System.out.println("Step " + graph.schedule.getSteps() + " : NEW " + p.toString());
        for (int i = sortedServices.size() / 2; i < sortedServices.size(); i++) {
            platform.getServices().remove(sortedServices.get(i));
        }
    }
}
