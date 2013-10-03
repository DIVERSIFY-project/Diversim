package diversim.strategy.reproduction;

import java.util.Collections;
import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;

public class PlatformReproductionWithPossibility implements PlatformReproductionStrategy{
	
	private double possibility = 0;
	private PlatformReproductionStrategy reproducer = null;
	
	public PlatformReproductionWithPossibility(double possiblility, PlatformReproductionStrategy reproducer){
		this.possibility = possiblility;
		this.reproducer = reproducer;
	}

	@Override
	public List<Platform> reproduce(Platform parent, BipartiteGraph state) {
	if (state.random.nextDouble() < possibility) {
			 return reproducer.reproduce(parent, state);
		 }
		 else
			 return Collections.emptyList();
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
