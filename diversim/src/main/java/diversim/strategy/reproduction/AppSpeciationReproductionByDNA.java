package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.model.App;
import diversim.model.BipartiteGraph;


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
		App child = state.createApp(this.speciator.speciate(parent.getDependencies(), state.services));
		ArrayList<App> children = new ArrayList<App>();
		children.add(child);
		return children;
	}

	

}
