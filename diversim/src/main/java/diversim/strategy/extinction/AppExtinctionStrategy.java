package diversim.strategy.extinction;

import diversim.App;
import diversim.BipartiteGraph;
import diversim.Entity;

public interface AppExtinctionStrategy {
	 public boolean die(App app, BipartiteGraph graph);
}
