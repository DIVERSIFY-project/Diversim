package diversim.strategy.reproduction;

import java.util.List;
import java.util.ArrayList;

import diversim.BipartiteGraph;
import diversim.Platform;
import ec.util.MersenneTwisterFast;

/**
 * This class implements the ReproductionStrategy with clonal reproduction
 * i.e., the Platform child that is created is exactly the same as the parent Platform. Also, this strategy results in only one child.
 * @author Vivek Nallur
 */
public class PlatformClonalReproduction implements PlatformReproductionStrategy{
        public List<Platform> reproduce(Platform parent, BipartiteGraph state){
			MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		  	Platform child = new Platform(rnd.nextInt(), parent.getSupportedServices());
			child.setLoadingFactor(parent.getLoadingFactor());
			ArrayList<Platform> children = new ArrayList<Platform>();
			children.add(child);
			return children;
	}
}
