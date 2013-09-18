package diversim.strategy.extinction;

import diversim.App;
import diversim.BipartiteGraph;
import diversim.Entity;
import diversim.Platform;

public interface PlatformExtinctionStrategy {
	 public boolean die(Platform platform, BipartiteGraph graph);
}
