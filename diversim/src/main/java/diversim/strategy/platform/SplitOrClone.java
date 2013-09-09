package diversim.strategy.platform;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.Strategy;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:46 AM
 */
public class SplitOrClone extends AbstractStrategy<Platform> {

    Strategy<Platform> clone;
    Strategy<Platform> split;
  double cFactor, sFactor;

    public SplitOrClone(String n, Split s, double sf, CloneMutate c, double cf) {
      super(n);
      clone = c;
      split = s;
      sFactor = sf;
      cFactor = cf;
    }


    @Override
    public void evolve(BipartiteGraph graph, Platform platform) {
    if (((double)platform.getSize()) >= sFactor * graph.getPlatformMinSize()) {
      split.evolve(graph, platform);
      platform.action = "combo->" + platform.action;
    } else if (((double)platform.getSize()) >= cFactor * graph.getPlatformMinSize()) {
      clone.evolve(graph, platform);
      platform.action = "combo->" + platform.action;
    }
}
}
