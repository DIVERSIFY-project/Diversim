package diversim;

import java.util.List;

import ec.util.MersenneTwisterFast;

/**
 * This is the interface that all App reproduction strategies must implement
 * @author Vivek Nallur
 */
public interface AppReproductionStrategy{
        public List<App> reproduce(App parent, List<App> other_apps);
}
