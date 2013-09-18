package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.App;
import diversim.BipartiteGraph;
import ec.util.MersenneTwisterFast;


/**
 * Randomly choose one of the registered speciation strategies
 * to change its dna
 * 
 * @author hui song
 *
 */

public class AppSpeciationReproductionByDNA implements AppReproductionStrategy {
	
	DNASpeciation speciator = null;
	
	public AppSpeciationReproductionByDNA(DNASpeciation speciator){
		this.speciator = speciator;
	}

	
	public List<App> reproduce(App parent, BipartiteGraph state) {
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		
	  	App child = new App(rnd.nextInt(), 
	  				this.speciator.speciate(parent.getDependencies(), state.services)
	  			);
	  	
	  	child.initStrategies(state);
		ArrayList<App> children = new ArrayList<App>();
		children.add(child);
		return children;
	}

	

}
