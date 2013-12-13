/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diversim.strategy.application;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author huis
 */
public class AllMatchingLinkStrategy extends AbstractStrategy<App> {

    public AllMatchingLinkStrategy() {
        super("allmatching");
    }

    @Override
    public void evolve(BipartiteGraph graph, App e) {
        removeLinkFor(graph, e);
        
        for(Platform p : graph.platforms){            
            if(p.getDegree() < graph.getPlatformMaxLoad() && p.services.containsAll(e.services)){
                graph.addEdge(e, p, e.getSize());
            }
        }
    }

    public void init(String stratId) {
        this.name = "allmatching";
    }

    protected void removeLinkFor(BipartiteGraph graph, App e) {
        // the graph is undirected, thus EdgesIn = EdgesOut
        Bag edges = graph.bipartiteNetwork.getEdgesIn(e); // read-only!

        for (Object edge : edges) {
            graph.removeEdge(e, (Edge) edge);
        }
    }
    
    /*
     * Adding a static version of evolve, for the robustness class to pick use this method, while calculating
     * the robustness values
     *
     */
    public static void allmatching(BipartiteGraph graph){
    	AllMatchingLinkStrategy strategy = new AllMatchingLinkStrategy();
    	for (App a: graph.apps){
    		strategy.evolve(graph, a);
    	}
    }
    

}
