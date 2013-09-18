package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.BipartiteGraph;
import diversim.Platform;
import diversim.Service;
import ec.util.MersenneTwisterFast;

public class PlatformSpeciationReproductionByDNA 
		implements PlatformReproductionStrategy {

	private DNASpeciation speciator;

	public PlatformSpeciationReproductionByDNA(DNASpeciation speciator){
		this.speciator = speciator;
	}
	
	@Override
	public List<Platform> reproduce(Platform parent,
			BipartiteGraph state) {
		// TODO Auto-generated method stub
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());		
		List<Service> services = speciator.speciate(parent.services, state.services);		
		List<Platform> children = new ArrayList<Platform>();
		Platform pltf = new Platform(rnd.nextInt(), services, parent.getLoadingFactor());
		pltf.initStategies(state);
		children.add(pltf);
		return children;
	}

}
