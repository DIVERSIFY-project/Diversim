package diversim.strategy.fate;


import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.strategy.AbstractStrategy;


/**
 * User: Andre Date: 26/9/13 Time: 10:45 AM
 */
public class PopularReproduction extends AbstractStrategy<Fate> {

public PopularReproduction() {
	super("Popular");
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {

}
}
