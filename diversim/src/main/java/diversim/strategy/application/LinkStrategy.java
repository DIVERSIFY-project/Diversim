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
public class LinkStrategy extends AbstractStrategy<Entity> {

    public LinkStrategy(String n) {
    super(n);
    // TODO Auto-generated constructor stub
  }

    public void evolve(BipartiteGraph graph, Entity e) {
        removeLinkFor(graph,e);

        Platform p = getBestPlatform(graph,e.getServices());
        if(p != null)
          Entity.addEdge(graph, e, p, e.getSize());
        else
            for(Platform platform : getPlatform(graph, e.getServices()))
              Entity.addEdge(graph, e, platform, platform.countCommonServices(e, null));
    }

    protected void removeLinkFor(BipartiteGraph graph, Entity e) {
        // the graph is undirected, thus EdgesIn = EdgesOut
        Bag edges = graph.bipartiteNetwork.getEdgesIn(e); // read-only!

        for (Edge edge : (Edge[]) edges.toArray(new Edge[edges.size()])) {
            graph.bipartiteNetwork.removeEdge(edge);
            Entity rem = (Entity) edge.getOtherNode(e);
            rem.decDegree();
            e.decDegree();
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
