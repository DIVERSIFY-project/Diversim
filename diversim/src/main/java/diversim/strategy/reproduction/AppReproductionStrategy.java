package diversim.strategy.reproduction;

import java.util.List;

import diversim.App;
import diversim.BipartiteGraph;

/**
 * This is the interface that all App reproduction strategies must implement
 * @author Vivek Nallur
 * @author Hui Song
 */
public interface AppReproductionStrategy{
        public List<App> reproduce(App parent, BipartiteGraph state);
}
