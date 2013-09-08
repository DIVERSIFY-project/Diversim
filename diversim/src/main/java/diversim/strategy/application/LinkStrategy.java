package diversim.strategy.application;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.field.network.Edge;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:59 AM
 */
public class LinkStrategy extends AbstractStrategy<App> {

    public LinkStrategy(String n) {
    super(n);
    // TODO Auto-generated constructor stub
  }

    public void evolve(BipartiteGraph graph, App app) {
        removeLinkFor(graph,app);

        Platform p = getBestPlatform(graph,app.getServices());
        if(p != null)
            addEdge(graph,app,p);
        else
            for(Platform platform : getPlatform(graph, app.getServices()))
                addEdge(graph,app,platform);
    }

    protected void removeLinkFor(BipartiteGraph graph, App app) {
        // the graph is undirected, thus EdgesIn = EdgesOut
        Bag edges = graph.bipartiteNetwork.getEdgesIn(app); // read-only!

        for (Edge edge : (Edge[]) edges.toArray(new Edge[edges.size()])) {
            graph.bipartiteNetwork.removeEdge(edge);
            Entity rem = (Entity) edge.getOtherNode(app);
            rem.decDegree();
            app.decDegree();
        }
    }

    protected Platform getBestPlatform(BipartiteGraph graph, List<Service> services) {
        Platform p = null;
        for(Platform platform : findPlatformWithAllServices(graph.platforms, services))
            if(platform.getDegree() < graph.getPlatformMaxLoad()) {
                p = platform;
                break;
            }
        return p;
    }

    protected List<Platform> getPlatform(BipartiteGraph graph, List<Service> services) {
        List<Platform> list = new ArrayList<Platform>();
        List<Service> tmp = new ArrayList<Service>(services);

        for(Platform platform : findPlatformWithServices(graph.platforms, services)) {
            if(platform.getDegree() < graph.getPlatformMaxLoad()) {
                int size = tmp.size();
                tmp.removeAll(platform.getServices());
                if(tmp.size() < size)
                list.add(platform);
            }
            if(tmp.isEmpty())
                break;
        }
        if(!tmp.isEmpty())
            return new ArrayList<Platform>();
        else
            return list;
    }

    protected List<Platform> findPlatformWithAllServices(List<Platform> platforms, List<Service> services) {
        List<Platform> list = new ArrayList<Platform>();
        for(Platform platform :platforms) {
            if(platform.getServices().containsAll(services))
                list.add(platform);
        }
        Collections.sort(list, new Comparator<Platform>() {
            @Override
            public int compare(Platform platform, Platform platform2) {
                return platform.getDegree() - platform2.getDegree() ;
            }
        });
        return list;
    }

    protected List<Platform> findPlatformWithServices(List<Platform> platforms, List<Service> services) {
        List<Platform> list = new ArrayList<Platform>();
        for(Platform platform :platforms) {
            for (Service service : services) {
                if(platform.getServices().contains(service)) {
                    list.add(platform);
                    break;
                }
            }
        }
        Collections.sort(list, new Comparator<Platform>() {
            @Override
            public int compare(Platform platform, Platform platform2) {
                return platform.getDegree() - platform2.getDegree() ;
            }
        });
        return list;
    }

    protected void addEdge(BipartiteGraph graph, App app, Platform p ) {
        graph.bipartiteNetwork.addEdge(app, p, 1);
        app.incDegree();
        p.incDegree();
        graph.changed();
    }
}
