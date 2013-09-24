package diversim.strategy.extinction;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;

public interface AppExtinctionStrategy {
	 public boolean die(App app, BipartiteGraph graph);
}
