package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.field.network.Edge;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:59 AM
 */
public class LinkStrategy extends AbstractStrategy<App> {

    public LinkStrategy(String n) {
    super(n);
  }

    public void evolve(BipartiteGraph graph, App e) {
        removeLinkFor(graph,e);

        Platform p = getBestPlatform(graph,e.getServices());
        if(p != null)
          graph.addEdge(e, p, e.getSize());
        else
            for(Platform platform : getPlatform(graph, e.getServices()))
              graph.addEdge(e, platform, platform.countCommonServices(e, null));
    }


protected void removeLinkFor(BipartiteGraph graph, App e) {
  // the graph is undirected, thus EdgesIn = EdgesOut
  Bag edges = graph.bipartiteNetwork.getEdgesIn(e); // read-only!

  for (Object edge : edges) {
    graph.removeEdge(e, (Edge)edge);
  }
}


    protected Platform getBestPlatform(BipartiteGraph graph, List<Service> services) {
        Platform p = null;
        for(Entity platform : Entity.findEntityWithAllServices(graph.platforms, services))
            if(platform.getDegree() <= graph.getPlatformMaxLoad()) {
                p = (Platform)platform;
                break;
            }
        return p;
    }


    protected List<Platform> getPlatform(BipartiteGraph graph, List<Service> services) {
        List<Platform> list = new ArrayList<Platform>();
        List<Service> tmp = new ArrayList<Service>(services);

        for(Entity platform : Entity.findEntityWithServices(graph.platforms, services)) {
            if(platform.getDegree() <= graph.getPlatformMaxLoad()) {
                int size = tmp.size();
                tmp.removeAll(platform.getServices());
                if(tmp.size() < size)
                list.add((Platform)platform);
            }
            if(tmp.isEmpty())
                break;
        }
        if(!tmp.isEmpty())
            return new ArrayList<Platform>();
        else
            return list;
    }
}
