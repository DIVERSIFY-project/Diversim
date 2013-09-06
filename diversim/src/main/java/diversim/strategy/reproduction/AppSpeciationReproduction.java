package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.App;
import ec.util.MersenneTwisterFast;


/**
 * Randomly choose one of the registered speciation strategies
 * to change its dna
 * 
 * @author hui song
 *
 */
public class AppSpeciationReproduction extends AbstractSpeciationReproduction implements AppReproductionStrategy {
	
	
	

	@Override
	public List<App> reproduce(App parent, List<App> other_apps) {
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		
		SpeciationStrategy strategy = getStrategies().get(rnd.nextInt(getStrategies().size()));
		
	  	App child = new App(rnd.nextInt(), 
	  			strategy.speciate(parent.getDependencies(),getAllServices())
	  			);
		ArrayList<App> children = new ArrayList<App>();
		children.add(child);
		return children;

	}

}
