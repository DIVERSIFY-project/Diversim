package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.strategy.Strategy;
import diversim.strategy.AbstractStrategy;


/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:26 PM
 */
public class FateStrategy extends AbstractStrategy<Fate> {

Strategy<Fate> killAppStrategy;
Strategy<Fate> addAppStrategy;


    public FateStrategy(String n, Strategy<Fate> add, Strategy<Fate> kill) {
      super(n);
        killAppStrategy = new diversim.strategy.NullStrategy<Fate>(); //kill;
        addAppStrategy = add;
    }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
        killAppStrategy.evolve(graph,agent);
        addAppStrategy.evolve(graph,agent);
    }
}
