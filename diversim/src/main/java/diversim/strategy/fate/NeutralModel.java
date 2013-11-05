package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.Strategy;
import diversim.util.config.Configuration;

/**
 * User: Simon
 * Date: 9/25/13
 * Time: 4:05 PM
 */
public class NeutralModel extends AbstractStrategy<Fate> {
protected Strategy<Fate> kill;
protected Strategy<Fate> add;

public NeutralModel() {
	super("NeutralModel");
}

@Override
public void evolve(BipartiteGraph graph, Fate agent)  {
	kill.evolve(graph,agent);
	add.evolve(graph,agent);
}

@Override
public void init(String stratId) {
	kill = (Strategy<Fate>) BipartiteGraph.getStrategy(Configuration.getString(stratId + ".kill"));
	add = (Strategy<Fate>) BipartiteGraph.getStrategy(Configuration.getString(stratId + ".add"));
}
}
