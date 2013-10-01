package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class PlatformSpeciationReproductionByDNA
		implements PlatformReproductionStrategy {

	private DNASpeciation speciator;

	public PlatformSpeciationReproductionByDNA(DNASpeciation speciator){
		this.speciator = speciator;
	}
	
	@Override
	public List<Platform> reproduce(Platform parent,
			BipartiteGraph state) {
		List<Service> services = speciator.speciate(parent.services, state.services);
		List<Platform> children = new ArrayList<Platform>();
		Platform pltf = state.createPlatform(services, parent.getLoadingFactor());
		children.add(pltf);
		return children;
	}

}
