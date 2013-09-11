package diversim.strategy.reproduction;

import java.util.Collections;
import java.util.List;

import diversim.App;
import diversim.BipartiteGraph;
import ec.util.MersenneTwisterFast;

public class AppReproductionWithPossibility implements AppReproductionStrategy{
	
	private double possibility = 0;
	private AppReproductionStrategy reproducer = null;
	
	public AppReproductionWithPossibility(double possiblility, AppReproductionStrategy reproducer){
		this.possibility = possiblility;
		this.reproducer = reproducer;
	}

	@Override
	public List<App> reproduce(App parent, BipartiteGraph state) {
		 MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		 if(rnd.nextDouble() < possibility){
			 return reproducer.reproduce(parent, state);
		 }
		 else
			 return Collections.emptyList();
	}
}
