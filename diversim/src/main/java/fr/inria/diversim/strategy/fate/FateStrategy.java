package fr.inria.diversim.strategy.fate;

import fr.inria.diversim.model.BipartiteGraph;
import fr.inria.diversim.model.Fate;
import ec.util.MersenneTwisterFast;
import fr.inria.diversim.strategy.Strategy;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:26 PM
 */
public class FateStrategy implements Strategy<Fate> {
    Strategy<Fate> killAppStrategy;
    Strategy<Fate> addAppStrategy;


    public FateStrategy(MersenneTwisterFast random) {
        killAppStrategy = new KillApp();
        addAppStrategy = new AddApp(random);
    }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
        killAppStrategy.evolve(graph,agent);
        addAppStrategy.evolve(graph,agent);
    }
}
