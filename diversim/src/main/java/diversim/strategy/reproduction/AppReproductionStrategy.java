package diversim.strategy.reproduction;

import java.util.List;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.strategy.Strategy;

/**
 * This is the interface that all App reproduction strategies must implement
 * @author Vivek Nallur
 * @author Hui Song
 */
public interface AppReproductionStrategy extends Strategy<App> {
        public List<App> reproduce(App parent, BipartiteGraph state);
}
