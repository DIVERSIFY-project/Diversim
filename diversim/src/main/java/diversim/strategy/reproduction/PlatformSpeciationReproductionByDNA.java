package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class PlatformSpeciationReproductionByDNA extends ReproStrategy<Platform> {

	private DNASpeciation speciator;

	public PlatformSpeciationReproductionByDNA(DNASpeciation speciator){
	super(""); // TODO
		this.speciator = speciator;
	}
	
	@Override
	public List<Platform> reproduce(Platform parent,
			BipartiteGraph state) {
		List<Service> services = speciator.speciate(parent.services, state.services);
		List<Platform> children = new ArrayList<Platform>();
	Platform pltf = state.createPlatform(""); // TODO
	pltf.setServices(services);
	pltf.setStrategy(parent.getStrategy()); // FIXME ??
	pltf.setDegree(parent.getDegree());
		children.add(pltf);
		return children;
	}

    @Override
    public void evolve(BipartiteGraph graph, Platform agent) {
      reproduce(agent, graph);
    }


@Override
public void init(String stratId) {
	// TODO Auto-generated method stub

}
}
