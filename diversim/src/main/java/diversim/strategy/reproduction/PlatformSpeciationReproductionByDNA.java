package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class PlatformSpeciationReproductionByDNA extends ReproStrategy<Platform> {
    public int max_size;
    public int min_size;

	private DNASpeciation speciator;

	public PlatformSpeciationReproductionByDNA(DNASpeciation speciator){
        super(speciator.getClass().getSimpleName()); // TODO
		this.speciator = speciator;
	}
	
	@Override
	public List<Platform> reproduce(Platform parent,
			BipartiteGraph state) {
		List<Service> services = speciator.speciate(parent.services, state.services);
        
        if(services.size() > max_size)
            services.remove(state.random.nextInt(services.size()));
        
        if(services.size() < min_size){
            ArrayList<Service> allServices = new ArrayList<Service>();
            allServices.addAll(state.services);
            allServices.removeAll(services);
            services.add(allServices.get(state.random.nextInt(allServices.size())));
        }
        
		List<Platform> children = new ArrayList<Platform>();
        String kind = state.platforms.get(0).getKind();
        Platform pltf = state.createPlatform(kind); // TODO
        pltf.services.clear();
        pltf.setServices(services);
        //pltf.setDegree(parent.getDegree());   //NOT-CLEAR
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
