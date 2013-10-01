package diversim.strategy.reproduction;

import java.util.Collections;
import java.util.List;

import diversim.model.App;
import diversim.model.BipartiteGraph;


public class AppReproductionWithPossibility implements AppReproductionStrategy{

	private double possibility = 0;
	private AppReproductionStrategy reproducer = null;

	public AppReproductionWithPossibility(double possiblility, AppReproductionStrategy reproducer){
		this.possibility = possiblility;
		this.reproducer = reproducer;
	}

	@Override
	public List<App> reproduce(App parent, BipartiteGraph state) {
		if(state.random.nextDouble() < possibility){
			return reproducer.reproduce(parent, state);
		}
		else
			return Collections.emptyList();
	}

    @Override
    public void evolve(BipartiteGraph graph, App agent) {
      reproduce(agent, graph);
    }
}
