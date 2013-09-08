package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.strategy.AbstractStrategy;


/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:26 PM
 */
public class FateStrategy extends AbstractStrategy<Fate> {

AbstractStrategy<Fate> killAppStrategy;
AbstractStrategy<Fate> addAppStrategy;


    public FateStrategy(String n) {
      super(n);
        killAppStrategy = new KillApp("Kill");
        addAppStrategy = new AddApp("Add");
    }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
        killAppStrategy.evolve(graph,agent);
        addAppStrategy.evolve(graph,agent);
    }
}
