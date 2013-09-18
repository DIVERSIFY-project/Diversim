package diversim.strategy.reproduction;

import java.util.Collections;
import java.util.List;

import diversim.App;
import diversim.BipartiteGraph;
import diversim.Platform;
import ec.util.MersenneTwisterFast;

public class PlatformReproductionWithPossibility implements PlatformReproductionStrategy{
	
	private double possibility = 0;
	private PlatformReproductionStrategy reproducer = null;
	
	public PlatformReproductionWithPossibility(double possiblility, PlatformReproductionStrategy reproducer){
		this.possibility = possiblility;
		this.reproducer = reproducer;
	}

	@Override
	public List<Platform> reproduce(Platform parent, BipartiteGraph state) {
		 MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		 if(rnd.nextDouble() < possibility){
			 return reproducer.reproduce(parent, state);
		 }
		 else
			 return Collections.emptyList();
	}
}
