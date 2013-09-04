package fr.inria.diversim.strategy.platform;

import fr.inria.diversim.model.BipartiteGraph;
import fr.inria.diversim.model.Platform;
import fr.inria.diversim.strategy.Strategy;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:46 AM
 */
public class MarcoStrategy implements Strategy<Platform> {
    Strategy clone;
    Strategy split;

    public MarcoStrategy() {
        clone = new CloneMutate();
        split = new Split();
    }


    @Override
    public void evolve(BipartiteGraph graph, Platform platform) {
//        action = "none";
        if (platform.getDegree() >= graph.getPlatformMaxLoad())
            if (platform.getSize() >= 2 * graph.getPlatformMinSize()) {
                clone.evolve(graph,platform);
//                action = "split_part";
            }
            else if (platform.getSize() >= graph.getPlatformMinSize()) {

                split.evolve(graph, platform);
//                action = "clone_mutate";
            }
    }
}
