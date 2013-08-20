package diversim;

import java.util.List;

import ec.util.MersenneTwisterFast;

/**
 * This interface must be implemented by all reproduction strategies of Platform
 * @author Vivek Nallur
 */
public interface PlatformReproductionStrategy{
        public List<Platform> reproduce(Platform parent, List<Platform> other_platforms);
}
