package diversim.strategy.extinction;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;

public interface PlatformExtinctionStrategy {
	 public boolean die(Platform platform, BipartiteGraph graph);
}
