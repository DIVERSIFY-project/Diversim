package diversim.strategy.platform;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.Strategy;
import sim.field.network.Edge;
import sim.util.Bag;

import java.util.ArrayList;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:21 AM
 */
public class Split extends AbstractStrategy<Platform> {

double ratio;

public Split(String n, double r) {
  super(n);
  ratio = r;
}


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
        Bag edges = graph.bipartiteNetwork.getEdges(this, null); // read-only!
        ArrayList<App> ents = new ArrayList<App>();
        for (Object o : edges) {
            ents.add((App)((Edge)o).getOtherNode(this));
        }
        // get the services used by the apps, sorted from the most to the least common
        ArrayList<Service> sortedServices = platform.sortServices(edges);
        int splitIndex = (int)Math.round(sortedServices.size() * ratio);

        // split the platform and keep here only the most shared half of the services
  @SuppressWarnings("unchecked")
  Platform p = graph.createPlatform(sortedServices.subList(splitIndex, sortedServices.size()));
        p.setStrategy((Strategy<Platform>)platform.getStrategy());
        graph.createLinks(p, ents);

        for (int i = splitIndex; i < sortedServices.size(); i++) {
            platform.getServices().remove(sortedServices.get(i)); // FIXME : make it O(log(services)) !!!
        }
        graph.updateLinks(platform);
        platform.action = "split_part";
        System.err.println(graph.getPrintoutHeader() + "Split : NEW " + p.toString());
    }
}
