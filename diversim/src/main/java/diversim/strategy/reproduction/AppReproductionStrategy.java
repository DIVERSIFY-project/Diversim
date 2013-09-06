package diversim.strategy.reproduction;

import java.util.List;

import diversim.App;
import ec.util.MersenneTwisterFast;

/**
 * This is the interface that all App reproduction strategies must implement
 * @author Vivek Nallur
 */
public interface AppReproductionStrategy{
        public List<App> reproduce(App parent, List<App> other_apps);
}
